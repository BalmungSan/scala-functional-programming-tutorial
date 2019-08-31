package co.edu.eafit.dis.progfun.cats.intro

import cats.Applicative // Import the Applicative type class.
import cats.data.ValidatedNec // Import the Validated[NonEmptyChain[E], _] data type.
import cats.instances.either._ // Brings the implicit Applicative[Either[E, _]] instance to scope.
import cats.instances.option._ // Brings the implicit Applicative[Option[_]] instance to scope.
import cats.syntax.apply._ // Provides the tupled and mapN methods.
import cats.syntax.option._ // Provides the option smart constructors.
import cats.syntax.validated._ // Provides the validated smart constructors.

object ApplicativeNotes extends App {
  println("- Applicatives & Validated -")
  println("-- Applicateives --")

  // Applicative allow us to join values inside a context,
  // in a less restrictive way than Monad's flatMap.
  // For example, the product method takes a F[A] and a F[B]
  // and returns a F[(A, B)].
  // However, no sequential order is guaranteed - unlike flatMap.
  val fa = 3.some
  val fb = "Hello, World!".some
  val fab = Applicative[Option[?]].product(fa, fb)
  println(s"Given fa = Some(3) & fb = Some('Hello, World!')\t->\tApplicative[Option[?]].product(fa, fb) = ${fab}")

  // We can map multiple values inside a context simultaneously,
  // using the map2 through map22 methods.
  val summed = Applicative[Option[?]].map3(1.some, 3.some, 5.some)(_ + _ + _)
  println(s"Applicative[Option[?]].map3(Some(1), Some(3), Some(5))(_ + _ + _) = ${summed}")

  // Cats provides convenient short hands versions of the tuple and map methods.
  val tupled = (1.some, "fun".some, true.some).tupled
  println(s"(Some(1), Some('fun'), Some(true)).tupled = ${tupled}")
  final case class Cat(name: String, born: Int, isMale: Boolean)
  println("final case class Cat(name: String, born: Int, isMale: Boolean)")
  val mapped =
    (
      "BalmungSan".some,
      1997.some,
      None
    ).mapN(Cat) // The Cat's constructor itself is a function.
  println(s"(Some('BalmungSan'), Some(1997), None).mapN(Cat) = ${mapped}")
  println()

  // Applicatives and Monads.
  // To ensure consistent semantics, Cats’ Monad provides a standard definition
  // of product in terms of map and flatMap.
  // `def product[F[_]: Monad, A, B](fa: F[A], fb: F[B]): F[(A, B)] = fa.flatMap(a => fb.map(b => (a, b)))`
  // This gives what we might think of as unexpected and less useful behavior
  // for a number of data types - for example Either.
  println("-- Applicatives & Monads --")
  val eitherErrors =
    Applicative[Either[List[String], ?]].product(
      Left(List("Error 1")),
      Left(List("Error 2"))
    )
  println(s"Applicative[Either[List[String], ?]].product(Left(List('Error 1')), Left(List('Error 2'))) = ${eitherErrors}")
  println()

  // Validated!
  // Validated is a cats' Data Type similar to Either that has an instance of Applicative,
  // but no instance of Monad. Thus, the implementation of product is free to accumulate errors.
  println("-- Validated --")
  val validatedErrors =
    Applicative[ValidatedNec[String, ?]].tuple3(
      "Error 1".invalidNec,
      10.validNec,
      "Error 2".invalidNec
    )
  println(s"Applicative[Validated[Chain[String], ?]].tuple3(Invalid('Error 1'), Valid(10), Invalid('Error 2')) = ${validatedErrors}")
}
