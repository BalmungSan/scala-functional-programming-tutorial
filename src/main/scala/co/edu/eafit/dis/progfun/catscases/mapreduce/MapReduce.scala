package co.edu.eafit.dis.progfun.catscases.mapreduce

import cats.Monoid
import scala.concurrent.{Await, Future}

import cats.instances.future._
import cats.instances.int._
import cats.instances.string._
import cats.instances.vector._
import cats.syntax.foldable._
import cats.syntax.traverse._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Parallel foldMap with more Cats.
  */
object MapReduce extends App {
  def parallelFoldMap[A, B: Monoid] (values: Vector[A])
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
    parallelFoldMap((1 to 1000).toVector)(_ * 1000)

  println(Await.result(future, 1.second))

  println(Await.result(
    parallelFoldMap(
      List(1, 2, 3, 4, 5, 6, 7, 8, 9).toVector)(_.toString), 1.second))
}