package co.edu.eafit.dis.progfun.io.intro

import cats.effect.IO

object IONotes extends App {
  IO(println("Hello, World")).unsafeRunSync()
}
