package co.edu.eafit.dis.progfun.io.tutorial

import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._

object CopyFile extends IOApp {
  override def run(args: List[String]): IO[ExitCode] =
    program.as(ExitCode.Success)

  final val program: IO[Unit] =
    IO(println("Hello, World"))
}
