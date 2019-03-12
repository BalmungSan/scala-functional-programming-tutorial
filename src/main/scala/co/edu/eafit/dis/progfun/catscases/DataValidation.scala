package co.edu.eafit.dis.progfun.catscases

import cats.Semigroup
import cats.syntax.semigroup._
import cats.syntax.either._
import cats.instances.list._
import cats.data.Validated
import cats.data.Validated._
import cats.syntax.apply._
import cats.syntax.validated._


/**
  * - [ ] Combine checks
  * - [ ] Accumulating errors as we check
  * - [ ] Transforming data as we check it
  **/
object DataValidation extends App {

  //Book's suggested implementation for Check
  sealed trait CheckF[E, A] {
    def and(that: CheckF[E, A]): CheckF[E, A] =
      AndF(this, that)

    def apply(a: A)(implicit s: Semigroup[E]): Either[E, A] =
      this match {
        case PureF(func) =>
          func(a)

        case AndF(left, right) =>
          (left(a), right(a)) match {
            case (Left(e1), Left(e2)) => (e1 |+| e2).asLeft
            case (Left(e), Right(a)) => e.asLeft
            case (Right(a), Left(e)) => e.asLeft
            case (Right(a1), Right(a2)) => a.asRight
          }
      }
  }

  // With an explicit data type for each combinator.
  final case class AndF[E, A](left: CheckF[E, A],
                             right: CheckF[E, A]) extends CheckF[E, A]

  final case class PureF[E, A](
                               func: A => Either[E, A]) extends CheckF[E, A]

  // Perform some validation
  val emailValidationA: CheckF[List[String], String] = PureF{ v =>
    if (v.split('@').length == 2) v.asRight
    else List("Must contain 1 '@'").asLeft
  }

  val emailValidationB: CheckF[List[String], String] = PureF { v =>
    if(v.contains(' ')) List("Must not contain blank spaces").asLeft
    else v.asRight
  }

  val check: CheckF[List[String], String] =
    emailValidationA and emailValidationB

  println(check("email@email.com")) //Success
  println(check("email.email.com"))
  println(check(" email@email.com"))
  println(check("email email.com")) //Both errors

  sealed trait Predicate[E, A] {
    import Predicate._ //Predicate's type classes defined below.
    def and(that: Predicate[E, A]): Predicate[E, A] =
      And(this, that)

    def or(that: Predicate[E, A]): Predicate[E, A] =
      Or(this, that)

    def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] =
      this match {
        case Pure(func) =>
          func(a)

        case And(left, right) =>
          (left(a), right(a)).mapN((_, _) => a)

        case Or(left, right) => left(a) match {
          case Valid(a) => Valid(a)
          case Invalid(e1) => right(a) match {
            case Valid(a) => Valid(a)
            case Invalid(e2) => Invalid(e1 |+| e2)
          }
        }
      }

    def run(a: A)(implicit s: Semigroup[E]): Either[E, A] = this(a).toEither
  }

object Predicate {
  final case class Pure[E, A](func: A => Validated[E, A])
    extends Predicate[E, A]

  final case class And[E, A](
                              left: Predicate[E, A],
                              right: Predicate[E, A]) extends Predicate[E, A]

  final case class Or[E, A](left: Predicate[E, A],
                            right: Predicate[E, A]) extends Predicate[E, A]

  def apply[E, A](f: A => Validated[E, A]): Predicate[E, A] = Pure(f)

  def lift[E, A](err: E, fn: A => Boolean): Predicate[E, A] =
    Pure(a => if(fn(a)) a.valid else err.invalid)
}

  sealed trait Check[E, A, B] {
    import Check._ //Check's type classes defined below
    def apply(in: A)(implicit s: Semigroup[E]): Validated[E, B]

    def map[C](f: B => C): Check[E, A, C] =
      Map[E, A, B, C](this, f)

    def flatMap[C](f: B => Check[E, A, C]) =
      FlatMap[E, A, B, C](this, f)

    def andThen[C](that: Check[E, B, C]): Check[E, A, C] =
      AndThen[E, A, B, C](this, that)
  }

  object Check {
    def apply[E, A](pred: Predicate[E, A]): Check[E, A, A] =
      Pure(pred)

    final case class Pure[E, A](pred: Predicate[E, A]) extends Check[E, A, A] {
      def apply(in: A)(implicit s: Semigroup[E]): Validated[E, A] =
        pred(in)
    }

    final case class Map[E, A, B, C](check: Check[E, A, B],
                                     func: B => C) extends Check[E, A, C] {
      def apply(in: A)(implicit s: Semigroup[E]): Validated[E, C] =
        check(in).map(func)
    }

    final case class FlatMap[E, A, B, C](check: Check[E, A, B],
                                         func: B => Check[E, A, C])
      extends Check[E, A, C] {
      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
        check(a).withEither(_.flatMap(b => func(b)(a).toEither))
    }

    final case class AndThen[E, A, B, C](check1: Check[E, A, B],
                                         check2: Check[E, B, C])
      extends Check[E, A, C] {
      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
        check1(a).withEither(_.flatMap(b => check2(b).toEither))
    }
  }
}