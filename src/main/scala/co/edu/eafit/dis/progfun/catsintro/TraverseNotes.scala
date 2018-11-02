package co.edu.eafit.dis.progfun.catsintro

import cats.Traverse // Import the Traverse type class.
import cats.instances.int._ // Brings the implicit Monoid[Int] instance to scope.
import cats.instances.list._ // Brings the implicit Traverse[List[_]] instance to scope.
import cats.syntax.foldable._ // Provides the combineAll and foldMap methods.

object TraverseNotes extends App {
  // Folding!
  // We can use the foldLeft and foldRight methods,
  // to combine all values in a collection.
  val folded1 = Traverse[List[?]].foldLeft(List(1, 2, 3), List.empty[Int]) {
    (acc, num) => num :: acc
  }
  println(s"Traverse[List[?]].foldLeft(List(1, 2, 3), Nil)(num :: acc) = ${folded1}")
  val folded2 = Traverse[List[?]].foldRight(List(1, 2, 3), cats.Eval.Zero) {
    (num, accEval) => accEval.map(acc => num + acc)
  }
  println(s"Traverse[List[?]].foldRight(List(1, 2, 3), 0)(num + acc) = ${folded2.value}")

  // CombineAll!
  // Usually we want to fold all values in a collection,
  // Using a default (or common) empty value and combine operation.
  // The combineAll method is a short hand for folding a collection of Ts
  // using a available Monoid[T].
  val folded3 = List(1, 2, 3).combineAll
  println(s"List(1, 2, 3).combineAll = ${folded3}")

  // FoldMap!
  // It is also very common to perform a transformation (map)
  // to the collection before combining the values.
  // e.g. `C[A].map(A => B).combineAll`
  // In those cases is better to use the foldMap method,
  // which will save us one iteration over the collection.
  val folded4 = List(1, 2, 3).foldMap(n => n * 2)
  println(s"List(1, 2, 3).foldMap(n => n * 2) = ${folded4}")
}
