package co.edu.eafit.dis.progfun.io.tutorial

import cats.effect.{Concurrent, ExitCase, ExitCode, IO, IOApp, Resource, Sync}
import cats.effect.syntax.bracket._ // Provides the bracketCase & guarantee methods.
import cats.effect.syntax.concurrent._ // Provides the start method.
import cats.syntax.flatMap._ // Provides the >> operator.

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

  private def serve[F[_]: Concurrent](serverSocket: ServerSocket): F[Unit] = {
    Sync[F]
      .delay(serverSocket.accept())
      .bracketCase { socket =>
        echoProtocol(socket)
          .guarantee(Sync[F].delay(socket.close())) // Ensure the socket is closed after use.
          .start
      } {
        // Handle adquisition errors.
        case (_, ExitCase.Completed) => Sync[F].unit
        case (socket, _)             => Sync[F].delay(socket.close())
      } >> serve(serverSocket) // Loop endlessly.
  }

  private def echoProtocol[F[_] : Sync](clientSocket: Socket): F[Unit] = {
    def loop(reader: BufferedReader, writer: PrintWriter): F[Unit] =
      Sync[F]
        .delay(reader.readLine())
        .flatMap {
          case "" =>
            Sync[F].unit

          case line =>
            Sync[F].delay(writer.println(line)) >>
            loop(reader, writer)
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
