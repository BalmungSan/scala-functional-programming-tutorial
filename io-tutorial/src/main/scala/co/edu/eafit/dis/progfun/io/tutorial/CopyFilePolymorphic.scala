package co.edu.eafit.dis.progfun.io.tutorial

import cats.effect.{Concurrent, ContextShift, ExitCase, ExitCode, IO, IOApp, Resource, Timer}
import cats.effect.syntax.bracket._ // Provides the guaranteeCase method.
import cats.effect.syntax.concurrent._ // Provides the start method.
import cats.syntax.flatMap._ // Provides the >> operator & the flatMap method.
import cats.syntax.functor._ // Provides the as method.

import java.io.{InputStream, OutputStream}
import java.nio.file.{Files, Path, Paths}
import scala.concurrent.duration._ // Provides the second method.

object CopyFilePolymorphic extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    args match {
      case List(originPath, destinationPath, bufferSize) =>
        CopyFilePolymorphicProgram[IO]
          .program(originPath, destinationPath, bufferSize)

      case _ =>
        IO {
          println(
            """IOCopyFile: Error: Bad number of arguments.
              |Usages: IOCopyFile origin destination bufferSize""".stripMargin
          )
        }.as(ExitCode.Error)
    }
}

class CopyFilePolymorphicProgram[F[_]](implicit F: Concurrent[F], CS: ContextShift[F], T: Timer[F]) {
  def program(originPath: String, destinationPath: String, bufferSize: String): F[ExitCode] =
    F.defer {
      val origin = Paths.get(originPath).toAbsolutePath()
      val destination = Paths.get(destinationPath).toAbsolutePath()

      if (!Files.exists(origin)) {
        raiseErrorMessage("IOCopyFile: Error: origin does not exists.")
      } else if (Files.exists(destination) && Files.isSameFile(origin, destination)) {
        raiseErrorMessage("IOCopyFile: Error: origin and destination are the same file.")
      } else bufferSize.toIntOption.filter(_ > 0) match {
        case None =>
          raiseErrorMessage("IOCopyFile: Error: bufferSize is not a valid number.")

        case Some(bufferSize) =>
          copy(origin, destination, bufferSize).flatMap { copiedBytes =>
            F.delay(println(s"IOCopyFile: Copy succesful. Copied ${copiedBytes} bytes."))
          }.guaranteeCase {
            case ExitCase.Canceled =>
              F.delay(println("IOCopyFile: Canceled!"))

            case _ =>
              F.unit
          }.as(ExitCode.Success)
      }
    }

  private def raiseErrorMessage(msg: String): F[ExitCode] =
    F.delay(println(msg)).as(ExitCode.Error)

  private def copy(origin: Path, destination: Path, bufferSize: Int): F[Long] =
    inputOutputStreams(origin, destination).use {
      case (in, out) => transfer(in, out, bufferSize)
    }

  private def inputOutputStreams(in: Path, out: Path): Resource[F, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

  private def inputStream(file: Path): Resource[F, InputStream] =
    Resource.fromAutoCloseable(
      F.delay(Files.newInputStream(file))
    )

  private def outputStream(file: Path): Resource[F, OutputStream] =
    Resource.makeCase {
      F.delay(Files.newOutputStream(file))
    } {
      case (stream, ExitCase.Completed) =>
        F.delay(stream.close())

      case (stream, ExitCase.Canceled | ExitCase.Error(_)) =>
        F.delay(stream.close()) >>
        F.delay(Files.delete(file))
    }

  private def transfer(origin: InputStream, destination: OutputStream, bufferSize: Int): F[Long] = {
    def transmit(buffer: Array[Byte], acc: Long): F[Long] =
      for {
        _ <- T.sleep(1.second)
        _ <- CS.shift // Checks for cancellation.
        amount <- F.delay(origin.read(buffer, 0, buffer.size))
        count  <- if(amount > -1) {
          F.delay(destination.write(buffer, 0, amount)) >>
          transmit(buffer, acc + amount)
        } else {
          F.pure(acc)
        }
      } yield count

    for {
      buffer <- F.delay(Array.ofDim[Byte](bufferSize))
      fiber <- transmit(buffer, acc = 0L).start
      total <- fiber.join
    } yield total
  }
}

object CopyFilePolymorphicProgram {
  def apply[F[_]: Concurrent : ContextShift : Timer]: CopyFilePolymorphicProgram[F] =
    new CopyFilePolymorphicProgram[F]
}
