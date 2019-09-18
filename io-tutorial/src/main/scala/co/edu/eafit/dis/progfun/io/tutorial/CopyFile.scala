package co.edu.eafit.dis.progfun.io.tutorial

import cats.effect.{ContextShift, ExitCase, ExitCode, IO, IOApp, Resource, Timer}
import cats.syntax.flatMap._
import cats.syntax.functor._

import java.io.{InputStream, OutputStream}
import java.nio.file.{Files, Path, Paths}
import scala.concurrent.duration._ // Provides the second method.

object CopyFile extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    args match {
      case List(originPath, destinationPath) =>
        Program.program(originPath, destinationPath).as(ExitCode.Success)

      case _ =>
        IO {
          println(
            """IOCopyFile: Bad number of arguments.
              |Usages: IOCopyFile origin destination""".stripMargin
          )
        }.as(ExitCode.Error)
    }
}

object Program {
  def program(originPath: String, destinationPath: String)
             (implicit ev1: ContextShift[IO], ev2: Timer[IO]): IO[Unit] =
    copy(
      origin = Paths.get(originPath),
      destination = Paths.get(destinationPath)
    ).flatMap { copiedBytes =>
      IO(println(s"Copy succesful. Copied ${copiedBytes} bytes."))
    }.guaranteeCase {
      case ExitCase.Canceled =>
        IO(println("Canceled!"))

      case _ =>
        IO.unit
    }

  private def copy(origin: Path, destination: Path)
                  (implicit ev1: ContextShift[IO], ev2: Timer[IO]): IO[Long] =
    inputOutputStreams(origin, destination).use {
      case (in, out) => transfer(in, out)
    }

  private def inputOutputStreams(in: Path, out: Path): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

  private def inputStream(file: Path): Resource[IO, InputStream] =
    Resource.fromAutoCloseable(
      IO(Files.newInputStream(file))
    )

  private def outputStream(file: Path): Resource[IO, OutputStream] =
    Resource.makeCase {
      IO(Files.newOutputStream(file))
    } {
      case (stream, ExitCase.Completed) =>
        IO(stream.close())

      case (stream, ExitCase.Canceled | ExitCase.Error(_)) =>
        IO(stream.close()) >>
        IO(Files.delete(file))
    }

  private def transfer(origin: InputStream, destination: OutputStream)
                      (implicit ev1: ContextShift[IO], ev2: Timer[IO]): IO[Long] = {
    def transmit(buffer: Array[Byte], acc: Long): IO[Long] =
      for {
        _ <- IO.sleep(1.second)
        _ <- IO.cancelBoundary // Checks for cancellation.
        amount <- IO(origin.read(buffer, 0, buffer.size))
        count  <- if(amount > -1) {
          IO.cancelBoundary >> // Checks for cancellation.
          IO(destination.write(buffer, 0, amount)) >>
          transmit(buffer, acc + amount)
        } else {
          IO.pure(acc)
        }
      } yield count

    for {
      buffer <- IO(new Array[Byte](10))
      fiber <- transmit(buffer, acc = 0L).start
      total <- fiber.join
    } yield total
  }
}
