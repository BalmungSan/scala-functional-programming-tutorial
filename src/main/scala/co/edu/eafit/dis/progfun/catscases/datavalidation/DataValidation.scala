package co.edu.eafit.dis.progfun.catscases.datavalidation

import cats.Semigroup

import cats.data.Validated._
import cats.data.Validated.{Invalid, Valid}
import cats.data.{Kleisli, NonEmptyList, Validated}
import cats.instances.either._ // for Semigroupal
import cats.instances.list._ // for Monad
import cats.syntax.apply._ // for mapN
import cats.syntax.validated._ // for valid and invalid
import cats.syntax.semigroup._ // for |+|

object DataValidation extends App {
  // Usage:
  val step1: Kleisli[List, Int, Int] =
    Kleisli(x => List(x + 1, x - 1))
  val step2: Kleisli[List, Int, Int] =
    Kleisli(x => List(x, -x))
  val step3: Kleisli[List, Int, Int] =
    Kleisli(x => List(x * 2, x / 2))

  val pipeline = step1 andThen step2 andThen step3
  println(pipeline.run(20).toString())

  /**
    * Predicate, includes the and and or combinators and a
    * Predicate.apply method to create a Predicate from a
    * function
    */
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

  type Result[A] = Either[Errors, A]

  type Check[A, B] = Kleisli[Result, A, B]

  // Create a check from a function.
  def check[A, B](func: A => Result[B]): Check[A, B] =
    Kleisli(func)

  // Create a check from a Predicate.
  def checkPred[A](pred: Predicate[Errors, A]): Check[A, A] =
    Kleisli[Result, A, A](pred.run)


  // Predicates.
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

  // Check username.
  val checkUsername: Check[String, String] =
    checkPred(longerThan(3) and alphanumeric)

  // Check Email.
  val splitEmail: Check[String, (String, String)] =
    check(_.split('@') match {
      case Array(name, domain) => Right(name, domain)
      case other => Left(error("Must contain a single @ character"))
    })

  val checkLeft: Check[String, String] =
    checkPred(longerThan(0))

  val checkRight: Check[String, String] =
    checkPred(longerThan(3) and contains('.'))

  val joinEmail: Check[(String, String), String] =
    check {
      case (l, r) =>
        (checkLeft(l), checkRight(r)).mapN(_ + "@" + _)
    }

  val checkEmail: Check[String, String] =
    splitEmail andThen joinEmail

  final case class User(username: String, email: String)

  def createUser(username: String, email: String): Either[Errors, User] =
    (checkUsername.run(username), checkEmail.run(email)).mapN(User)

  // Perform some checks.
  println(createUser("Noel", "Noel@underscore.io").toString)
  println(createUser("", "dave@underscore@io").toString)
}