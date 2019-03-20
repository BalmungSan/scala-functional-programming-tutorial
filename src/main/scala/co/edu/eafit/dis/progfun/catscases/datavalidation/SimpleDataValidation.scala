package co.edu.eafit.dis.progfun.catscases.datavalidation

import cats.Semigroup

import cats.data.Validated.{Invalid, Valid}
import cats.data.{NonEmptyList, Validated}
import cats.data.Validated._
import cats.syntax.apply._
import cats.syntax.semigroup._
import cats.syntax.validated._
import cats.syntax.either._
import cats.instances.list._

/**
  * - [ ] Combine checks
  * - [ ] Accumulating errors as we check
  * - [ ] Transforming data as we check it
  **/
object SimpleDataValidation extends App {

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

  type Errors = NonEmptyList[String]

  def error(s: String): NonEmptyList[String] =
    NonEmptyList(s, Nil)

  // Use lift as a constructor.
  def longerThan(n: Int): Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must be longer than $n characters"),
      str => str.size > n)

  val alphanumeric: Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must be all alphanumeric characters"),
      str => str.forall(_.isLetterOrDigit))

  def contains(char: Char): Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must contain the character $char"),
      str => str.contains(char))

  def containsOnce(char: Char): Predicate[Errors, String] =
    Predicate.lift(
      error(s"Must contain the character $char only once"),
      str => str.filter(c => c == char).size == 1)


  // A username must contain at least four characters
  // and consist entirely of alphanumeric characters
  val checkUsername: Check[Errors, String, String] =
  Check(longerThan(3) and alphanumeric)

  final case class User(username: String)

  def createUser(username: String, email: String): Validated[Errors, User] =
    // How do we check emails ??
    checkUsername(username).map(User)

  createUser("Noel", "noel@underscore.io")
  createUser("", "dave@underscore@io")
}
