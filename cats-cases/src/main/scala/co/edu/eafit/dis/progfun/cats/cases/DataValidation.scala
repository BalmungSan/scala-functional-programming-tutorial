package co.edu.eafit.dis.progfun.cats.cases

import cats.data.{Kleisli, Validated, ValidatedNec}
import cats.syntax.validated._ // For the valid and invalid methods.
import cats.syntax.apply._ // Provides the *> and mapN methods.

object DataValidation extends App {
  // Type aliases.
  type Result[A] = ValidatedNec[String, A]
  type Check[A, B] = Kleisli[Result, A, B]

  // Creates a Kleisli from a Function.
  def check[A, B](func: A => Result[B]): Check[A, B] =
    Kleisli(func)

  // Util validation functions.
  def longerThan(fieldName: String, n: Int): Check[String, String] =
    check { str =>
      Validated.condNec(
        str.size > 3,
        str,
        s"The '${fieldName}' must be longer than ${n} characters"
      )
    }

  def alphanumeric(fieldName: String): Check[String, String] =
    check { str =>
      Validated.condNec(
        str.forall(_.isLetterOrDigit),
        str,
        s"The '${fieldName}' must be all alphanumeric characters"
      )
    }

  def contains(fieldName: String, char: Char): Check[String, String] =
    check { str =>
      Validated.condNec(
        str.contains(char),
        str,
        s"The '${fieldName}' must contain the character '${char}'"
      )
    }

  // Checks username.
  val checkUsername: Check[String, String] = {
    val fieldName = "username"
    longerThan(fieldName, 3) *> alphanumeric(fieldName)
  }

  // Checks Email.
  val checkEmail: Check[String, String] = {
    val splitEmail: Check[String, (String, String)] =
      check { email =>
        email.split('@') match {
          case Array(name, domain) => (name, domain).valid
          case _                   => "The 'email' must contain a single '@' character".invalidNec
        }
      }

    val checkEmailName: Check[String, String] = {
      val fieldName = "email name"
      longerThan(fieldName, 0) *> alphanumeric(fieldName)
    }

    val checkEmailDomain: Check[String, String] = {
      val fieldName = "email domain"
      longerThan(fieldName, 3) *> contains(fieldName, '.')
    }

    check { str =>
      splitEmail(str) andThen {
        case (name, domain) =>
          (
            checkEmailName(name),
            checkEmailDomain(domain)
          ).mapN(_ + "@" + _) // Reconstruct the email.
      }
    }
  }

  // Represents an User of the system.
  final case class User(username: String, email: String)

  // Validates the User's registration information.
  def createUser(username: String, email: String): Result[User] =
    (
      checkUsername(username),
      checkEmail(email)
    ).mapN(User)

  val validated1 = createUser(username = "Noel", email = "Noel@underscoreio")
  println(s"createUser('Noel', 'Noel@underscoreio') = ${validated1}")
  val validated2 = createUser(username = "", email = "dave@underscore@io")
  println(s"createUser('', 'dave@underscore@io') = ${validated2}")
  val validated3 = createUser(username = "BalmungSan", email = "lmejias@psl.com.co")
  println(s"createUser('BalmungSan', 'lmejias@psl.com.co') = ${validated3}")
}
