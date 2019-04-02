package co.edu.eafit.dis.progfun.catsintro

import cats.Monoid // Import the Monoid type class.
import cats.instances.int._ // Brings the implicit Monoid[Int] and Eq[Int] instances to scope.
import cats.syntax.eq._ // Provides the === operator for type safe equality checks.
import cats.syntax.semigroup._ // Provides the |+| operator for combining two Monoids.

object MonoidNotes extends App {
  // The Semigroup typeclass encodes the ability to
  // combine two elements of the same type.
  // A Monoid is a Semigroup that also provides
  // a default empty element of such type.
  println("- Monoid -")
  println("-- The combine (|+|) function & the empty element --")

  // Combine two ints using the |+| operator.
  val combined = 1 |+| 2
  println(s"1 |+| 2 = ${combined}")

  // Probe the previous operation is equivalent to calling the combine method explicitly.
  val assertion1 = (1 |+| 2) === Monoid[Int].combine(1, 2)
  println(s"(1 |+| 2) === Monoid[Int].combine(1, 2) is ${assertion1}")

  // Ensure the cat's Monoid for ints uses the integer addition as the combine operation
  // and the integer's zero as the empty element.
  val assertion2 = (1 |+| Monoid[Int].empty) === (1 + 0)
  println(s"(1 |+| Monoid[Int].empty) === (1 + 0) is ${assertion2}")
  println()

  // Generic functions!
  // We can use the Monoid typeclass to create a generic function
  // for adding together all elements in a List.
  println("-- Generic functions using Monoid --")
  def addAll[T](elements: List[T])(implicit TMonoid: Monoid[T]): T =
    elements.foldLeft(TMonoid.empty) { case (a, b) => a |+| b }
  println("Given addAll[T](elements: List[T])(implicit TMonoid: Monoid[T]): T = elements.foldLeft(TMonoid.empty)((a, b) => a |+| b)")
  val summed1 = addAll(List(1, 2, 3))
  println(s"\taddAll(List(1, 2, 3)) = ${summed1}")

  // Let's define our own Monoid for Options.
  implicit def OptionMonoid[T](implicit TMonoid: Monoid[T]): Monoid[Option[T]] =
    new Monoid[Option[T]] {
      override val empty: Option[T] = None
      override def combine(e1: Option[T], e2: Option[T]): Option[T] = (e1, e2) match {
        case (Some(a), Some(b)) => Some(TMonoid.combine(a, b))
        case (Some(a), None)    => Some(a)
        case (None, Some(b))    => Some(b)
        case (None, None)       => None
      }
    }
  val summed2 = addAll(List(Some(1), None, Some(2), Some(3), None))
  println(s"\taddAll(List(Some(1), None, Some(2), Some(3), None)) = ${summed2}")
}
