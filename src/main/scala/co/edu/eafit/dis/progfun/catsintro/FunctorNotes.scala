package co.edu.eafit.dis.progfun.catsintro

import cats.Functor // Import the Functor type class.
import cats.instances.function._ // Brings the implicit Functor[X => ?] instance to scope.
import cats.instances.int._ // Brings the implicit Eq[Int] instance to scope.
import cats.instances.list._ // Brings the implicit Functor[List[?]] instance to scope.
import cats.syntax.eq.catsSyntaxEq // Provides the === operator for type safe equality checks.
import cats.syntax.functor.toFunctorOps // Provides the map operator for mapping a Functor.

object FunctorNotes extends App {
  // Map allow us to sequence computations inside a context.
  val mapped =
    List(1, 2, 3)
      .map(x => x + 1)
      .map(x => x * 2)
      .map(x => s"${x}!")
  println(s"List(1, 2, 3).map(x => x + 1).map(x => x * 2).map(x => s'$${x}!') = ${mapped}")

  // This common behavior is encapsulated in the Functor[F[_]] type class.
  val assertion = List(1, 2, 3).map(x => x + 1) === Functor[List].map(List(1, 2, 3))(x => x + 1)
  println(s"List(1, 2, 3).map(x => x + 1) === Functor[List].map(List(1, 2, 3))(x => x + 1) is ${assertion}")

  // Note that the 'map' method in Functor is curryfied, thus we could use it to
  // encapsulate the data we would like to map, and return a function,
  // which requires another function as an input and returns the mapped data.
  val mappeable: (Int => Int) => List[Int] = Functor[List].map(List(1, 2, 3)) _
  val mapped2 = mappeable(x => x + 1)
  println(s"Given mappeable = Functor[List].map(List(1, 2, 3))\t->\tmappeable(x => x + 1) = ${mapped2}")
  // This is very powerful. However, since a function can't be polymorphic (only methods),
  // we are forced to specify the output type of the inner function (which must be the same of the output type parameter).
  // If we don't specify this value, we would end with the followig type: (Int => Nothing) => List[Nothing], which is absolute useless.
  // We would like it to have the following type signature: [T] -> (Int => T) => List[T], and resolve the T type parameter in each call site.
  // Hope dotty#4672 gets merged for Scala 3 - which adds this functionality. https://github.com/lampepfl/dotty/pull/4672

  // We could also use the Functors 'lift' method to transform a function A => B into a function F[A] => F[B]
  val fun: Int => Int = x => x + 1
  val lifted: List[Int] => List[Int] = Functor[List].lift(fun)
  val mapped3 = lifted(List(1, 2, 3))
  println(s"Given lifted = Functor[List].lift(x => x + 1)\t->\tlifted(List(1, 2, 3)) = ${mapped3}")
  // Once again, this is very powerful. However, again we are limited in our expressiveness,
  // Since we can't have polymorphic functions, we need to specify the context for which this function is lifted.
  // But we would like to create a function with the following type signature:
  // [A, B, F[_]] -> (A => B) => (implicit Functor[F]) => (F[A] => F[B])
}
