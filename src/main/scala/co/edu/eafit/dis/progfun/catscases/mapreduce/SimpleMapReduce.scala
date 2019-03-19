package co.edu.eafit.dis.progfun.catscases.mapreduce

import cats.Monoid
import scala.concurrent.{Await, Future}

import cats.instances.int._
import cats.instances.string._
import cats.syntax.semigroup._ // for |+|
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object SimpleMapReduce extends App {

  /**
    * Here is a single thread implementation of foldMap
    * This alternative def does all to work in one step using
    * Semigroup's |+| (combine) operator
    */
  def foldMap[A, B : Monoid](values: Vector[A])(func: A => B): B =
    values.foldLeft(Monoid[B].empty)(_ |+| func(_))
  println("Single threaded foldMap --> ")
  println(foldMap(Vector(1, 2, 3))(identity))
  println(foldMap(Vector(1, 2, 3))(_.toString + "! "))
  println(foldMap("Hello World!".toVector)(_.toString.toUpperCase))

  /**
    * Now we are going to implement parallelFoldMap using Futures.
    * Monad for the map phase
    * Monoid for the reduce phase
    */
  def parallelFoldMap[A, B: Monoid] (values: Vector[A])
                                    (func: A => B): Future[B] = {
    // Split the data.
    val batches =
      if(values.length > Runtime.getRuntime.availableProcessors()) {
        values.grouped(
          Math.floorDiv(values.length, Runtime.getRuntime.availableProcessors()))
      } else {
        Vector(values)
      }

    val mapReduce = for {
      batch <- batches
    } yield Future(foldMap(batch)(func))

    // Main reduce.
    for {
      iterable <- Future.sequence(mapReduce)
    } yield iterable.foldLeft(Monoid[B].empty)(Monoid[B].combine)
  }
  println("Parallel foldMap --> ")
  println(Await.result(
    parallelFoldMap(
      List(1, 2, 3, 4, 5, 6, 7, 8, 9).toVector)(_.toString), 1.second))
}
