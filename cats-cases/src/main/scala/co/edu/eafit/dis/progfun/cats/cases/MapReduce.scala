package co.edu.eafit.dis.progfun.cats.cases

import cats.Monoid
import cats.instances.int._ // Brings the implicit Monoid[Int] instance to scope.
import cats.instances.string._ // Brings the implicit Monoid[String] instance to scope.
import cats.instances.vector._ // Brings the implicit Traverse[Vector[_]] instance to scope.
import cats.syntax.foldable._ // Provides the combineAll method.
import cats.syntax.monoid._ // Provides the |+| operator for combining two Monoids.
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

object MapReduce extends App {
  // Here is a single thread implementation of foldMap.
  // This alternative def does all to work in one step using
  // the Semigroup's |+| (combine) operator.
  def foldMap[A, B: Monoid](values: Vector[A])(func: A => B): B =
    values.foldLeft(Monoid[B].empty) { case (acc, a) => acc |+| func(a) }

  println("- Serial foldMap -")
  val foldMapped1 = foldMap(Vector(1, 2, 3))(identity)
  println(s"foldMap(Vector(1, 2, 3))(identity) = ${foldMapped1}")
  val foldMapped2 = foldMap(Vector(1, 2, 3))(_.toString + "! ")
  println(s"foldMap(Vector(1, 2, 3))(_.toString + '! ') = ${foldMapped2}")
  val foldMapped3 = foldMap("Hello, World!".toVector)(_.toString.toUpperCase)
  println(s"foldMap('Hello, World!')(_.toString.toUpperCase) = ${foldMapped3}")
  println()

  // Now we are going to implement parallelFoldMap using Futures.
  // Monad for the map phase.
  // Monoid for the reduce phase.
  def parallelFoldMap[A, B: Monoid](values: Vector[A])(func: A => B): Future[B] = {
    // Split the data.
    val numCores = Runtime.getRuntime.availableProcessors
    val groupSize = (values.size.toDouble / numCores.toDouble).ceil.toInt
    val batches = values.grouped(groupSize).toVector

    // Apply map-reduce to each batch in parallel.
    val mapReduce = for {
      batch <- batches
    } yield Future(foldMap(batch)(func))

    // Combine all intermediate results.
    Future.sequence(mapReduce).map(results => results.combineAll)
  }

  println("- Parallel foldMap-")
  val parallelFoldMapped1 =
    Await.result(
      parallelFoldMap(Vector(1, 2, 3, 4, 5, 6, 7, 8, 9))(_.toString),
      Duration.Inf
    )
  println(s"parallelFoldMap(Vector(1, 2, 3, 4, 5, 6, 7, 8, 9))(_.toString) = ${parallelFoldMapped1}")
  val parallelFoldMapped2 =
    Await.result(
      parallelFoldMap(Vector.range(start = 0, end = 1000))(_ * 1000),
      Duration.Inf
    )
  println(s"parallelFoldMap(0 to 1000)(_ * 1000) = ${parallelFoldMapped2}")
  val parallelFoldMapped3 =
    Await.result(
      parallelFoldMap("Hello, World!".toVector)(_.toString.toUpperCase),
      Duration.Inf
    )
  println(s"parallelFoldMap('Hello, World!')(_.toString.toUpperCase) = ${parallelFoldMapped3}")
}
