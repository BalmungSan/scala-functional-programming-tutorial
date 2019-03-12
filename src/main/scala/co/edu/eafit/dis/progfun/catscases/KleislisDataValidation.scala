package co.edu.eafit.dis.progfun.catscases

import cats.syntax.apply._ // For mapN
import cats.data.{Kleisli, NonEmptyList, Validated}
import cats.instances.either._ // for Semigroupal
import cats.instances.list._ // for Monad
import co.edu.eafit.dis.progfun.catscases.DataValidation.Predicate


object KleislisDataValidation extends App {
  val step1: Kleisli[List, Int, Int] =
    Kleisli(x => List(x + 1, x - 1))
  val step2: Kleisli[List, Int, Int] =
    Kleisli(x => List(x, -x))
  val step3: Kleisli[List, Int, Int] =
    Kleisli(x => List(x * 2, x / 2))

  val pipeline = step1 andThen step2 andThen step3
  println(pipeline.run(20).toString())


  type Result[A] = Either[Errors, A]

  type Check[A, B] = Kleisli[Result, A, B]

  // Create a check from a function:
  def check[A, B](func: A => Result[B]): Check[A, B] =
    Kleisli(func)

  // Create a check from a Predicate:
  def checkPred[A](pred: Predicate[Errors, A]): Check[A, A] =
    Kleisli[Result, A, A](pred.run)

  // Predicates
  import cats.data.{NonEmptyList, Validated}
  type Errors = NonEmptyList[String]

  def error(s: String): NonEmptyList[String] =
    NonEmptyList(s, Nil)

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

  val checkUsername: Check[String, String] =
    checkPred(longerThan(3) and alphanumeric)

  // Check Email
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

  def createUser(
                  username: String,
                  email: String): Either[Errors, User] = (
    checkUsername.run(username),
    checkEmail.run(email)
  ).mapN(User)

  // Perform some check
  println(createUser("Noel", "Noel@underscore.io").toString)
  println(createUser("", "dave@underscore@io").toString)
}
