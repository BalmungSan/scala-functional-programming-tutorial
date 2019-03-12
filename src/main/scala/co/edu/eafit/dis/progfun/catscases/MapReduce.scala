package co.edu.eafit.dis.progfun.catscases

import cats.Monoid
import scala.concurrent._
import scala.concurrent.{Await, Future}
import scala.concurrent.Future
import cats.instances.string._
import cats.syntax.semigroup._
import cats.instances.int._
import cats.instances.vector._
import cats.syntax.foldable._
import cats.syntax.traverse._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import cats.instances.future._

object MapReduce extends App {
  /**
    * Here is a single-thread implementation for foldMap
    */
  def foldMap[A, B: Monoid](values: Vector[A])(f: A => B): B = {
    val vectorOfB: Vector[B] = values.map(f)
    vectorOfB.foldLeft(Monoid[B].empty)(Monoid[B].combine)
  }
  println(foldMap(Vector(1, 2, 3))(identity))
  println(foldMap(Vector(1, 2, 3))(_.toString + "! "))
  println(foldMap("Hello World!".toVector)(_.toString.toUpperCase))

  /**
    * This alternative def does all to work in one step using
    * Semigroup's combine |+| operator
    */
  def foldMap_b[A, B : Monoid](as: Vector[A])(func: A => B): B =
    as.foldLeft(Monoid[B].empty)(_ |+| func(_))

  /**
    * Now we are going to implement parallelFoldMap using Futures.
    * Monad for the map phase and Monoid for the reduce phase
    */
  def parallelFoldMap[A, B: Monoid] (values: Vector[A])
                                    (func: A => B): Future[B] = {
    val batches = if(values.length > Runtime.getRuntime.availableProcessors()) {
      values.grouped(
        Math.floorDiv(values.length, Runtime.getRuntime.availableProcessors()))
    } else {
      Vector(values)
    }

    val mapReduce = for {
      batch <- batches
    } yield Future(foldMap_b(batch)(func))

    //Main reduce
    for {
      iterable <- Future.sequence(mapReduce)
    } yield iterable.foldLeft(Monoid[B].empty)(Monoid[B].combine)
  }

  println(Await.result(
    parallelFoldMap(
      List(1, 2, 3, 4, 5, 6, 7, 8, 9).toVector)(_.toString), 1.second))

  //Parallel foldMap with more Cats
  def catsParallelFoldMap[A, B: Monoid] (values: Vector[A])
                                        (func: A => B): Future[B] = {
    val batches = if(values.length > Runtime.getRuntime.availableProcessors()) {
      values.grouped(
        Math.floorDiv(values.length, Runtime.getRuntime.availableProcessors()))
    } else {
      Vector(values)
    }

    batches
      .toVector
      .traverse(group => Future(group.toVector.foldMap(func)))
      .map(_.combineAll)
  }

  val future: Future[Int] =
    catsParallelFoldMap((1 to 1000).toVector)(_ * 1000)
  println(Await.result(future, 1.second))
}