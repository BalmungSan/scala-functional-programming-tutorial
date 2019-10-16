package co.edu.eafit.dis.progfun.io.tutorial

import cats.effect.{Concurrent, ExitCase, ExitCode, IO, IOApp, Resource, Sync}
import cats.effect.concurrent.MVar
import cats.effect.syntax.bracket._ // Provides the bracketCase & guarantee methods.
import cats.effect.syntax.concurrent._ // Provides the start method.
import cats.syntax.flatMap._ // Provides the >> operator.
import cats.syntax.functor._ // Provides the map method.
import cats.syntax.applicativeError._ // Provides the attempt method.

import java.io.{BufferedReader, InputStreamReader, PrintWriter}
import java.net.{ServerSocket, Socket}

object TCPServer extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    TCPServerProgram.program[IO](args).attempt.flatMap {
      case Right(()) => IO(ExitCode.Success)
      case Left(ex)  => IO(println(ex.getMessage)) >> IO(ExitCode.Error)
    }
}

object TCPServerProgram {
  def program[F[_] : Concurrent](args: List[String]): F[Unit] =
    args match {
      case List(arg) =>
        arg.toIntOption.filter(port => (port >= 1024) && (port <= 65535)) match {
          case Some(port) =>
            Resource.fromAutoCloseable(
              Sync[F].delay(new ServerSocket(port))
            ).use { serverSocket =>
              Sync[F].delay(println("Starting server.")) >>
              serve(serverSocket)
            }

          case None =>
            Sync[F].raiseError(
              new RuntimeException(s"${arg} is not a valid port number.")
            )
        }

      case _ =>
        Sync[F].raiseError(
          new RuntimeException("Bad number of arguments.")
        )
    }

  private def serve[F[_]: Concurrent](serverSocket: ServerSocket): F[Unit] =
    for {
      stopFlag <- MVar[F].empty[Unit]
      serverFiber <- server(serverSocket, stopFlag).start
      _ <- stopFlag.read
      _ <- serverFiber.cancel.start
    } yield ()

  private def server[F[_]: Concurrent](serverSocket: ServerSocket, stopFlag: MVar[F, Unit]): F[Unit] = {
    Sync[F]
      .delay(serverSocket.accept())
      .bracketCase { socket =>
        echoProtocol(socket, stopFlag)
          .guarantee(Sync[F].delay(socket.close())) // Ensure the socket is closed after use or cancellation.
          .start
      } {
        // Handle adquisition errors.
        case (_, ExitCase.Completed) => Sync[F].unit
        case (socket, _)             => Sync[F].delay(socket.close())
      }.flatMap { fiber =>
        (stopFlag.read >> fiber.cancel).start
      } >> serve(serverSocket) // Loop endlessly.
  }

  private def echoProtocol[F[_] : Sync](clientSocket: Socket, stopFlag: MVar[F, Unit]): F[Unit] = {
    def loop(reader: BufferedReader, writer: PrintWriter): F[Unit] =
      Sync[F]
        .delay(reader.readLine())
        .attempt
        .flatMap {
          case Right("") =>
            Sync[F].unit

          case Right("STOP!") =>
            stopFlag.put(())

          case Right(line) =>
            Sync[F].delay(writer.println(line)) >>
            loop(reader, writer)

          case Left(ex) =>
            // readLine() failed.
            // stopFlag will tell us whether this is a graceful shutdown, or not.
            stopFlag.isEmpty.flatMap {
              case true  => Sync[F].delay(println("Stoping server."))
              case false => Sync[F].raiseError(ex)
            }
        }

    def reader(clientSocket: Socket): Resource[F, BufferedReader] =
      Resource.fromAutoCloseable(
        Sync[F].delay(
          new BufferedReader(
            new InputStreamReader(
              clientSocket.getInputStream()
            )
          )
        )
      )

    def writer(clientSocket: Socket): Resource[F, PrintWriter] =
      Resource.fromAutoCloseable(
        Sync[F].delay(
          new PrintWriter(
            clientSocket.getOutputStream(),
            true // autoflush.
          )
        )
      )

    def readerWriter(clientSocket: Socket): Resource[F, (BufferedReader, PrintWriter)] =
      for {
        reader <- reader(clientSocket)
        writer <- writer(clientSocket)
      } yield (reader, writer)

    readerWriter(clientSocket).use {
      case (reader, writer) => loop(reader, writer)
    }
  }
}
