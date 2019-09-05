package co.edu.eafit.dis.progfun.io.tutorial

import cats.effect.{ExitCode, IO, IOApp, Resource}
import cats.syntax.flatMap._
import cats.syntax.functor._

import java.io.{InputStream, File, FileInputStream, FileOutputStream, OutputStream}
import java.nio.file.Paths

object CopyFile extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    args match {
      case List(originPath, destinationPath) =>
        program(originPath, destinationPath).as(ExitCode.Success)

      case _ =>
        IO {
          println(
            """IOCopyFile: Bad number of arguments.
              |Usages: IOCopyFile origin destination""".stripMargin
          )
        }.as(ExitCode.Error)
    }

  def program(originPath: String, destinationPath: String): IO[Unit] =
    copy(
      origin = Paths.get(originPath).toFile,
      destination = Paths.get(destinationPath).toFile
    ).flatMap { copiedBytes =>
      IO(println(s"Copy succesful. Copied ${copiedBytes} bytes."))
    }

  def copy(origin: File, destination: File): IO[Long] =
    inputOutputStreams(in = origin, out = destination).use {
      case (origin, destination) => transfer(origin, destination)
    }

  def inputStream(f: File): Resource[IO, FileInputStream] =
    Resource.make {
      IO(new FileInputStream(f))
    } { inStream =>
      IO(inStream.close()).handleErrorWith(_ => IO.unit)
    }

  def outputStream(f: File): Resource[IO, FileOutputStream] =
    Resource.make {
      IO(new FileOutputStream(f))
    } { outStream =>
      IO(outStream.close()).handleErrorWith(_ => IO.unit)
    }

  def inputOutputStreams(in: File, out: File): Resource[IO, (InputStream, OutputStream)] =
    for {
      inStream  <- inputStream(in)
      outStream <- outputStream(out)
    } yield (inStream, outStream)

def transfer(origin: InputStream, destination: OutputStream): IO[Long] =
  for {
    buffer <- IO(new Array[Byte](1024 * 10)) // Allocated only when the IO is evaluated
    total  <- transmit(origin, destination, buffer, 0L)
  } yield total

def transmit(origin: InputStream, destination: OutputStream, buffer: Array[Byte], acc: Long): IO[Long] =
  for {
    amount <- IO(origin.read(buffer, 0, buffer.size))
    count  <- if(amount > -1) {
      IO(destination.write(buffer, 0, amount)) >>
      transmit(origin, destination, buffer, acc + amount)
    } else {
      IO.pure(acc)
    }
  } yield count
}
