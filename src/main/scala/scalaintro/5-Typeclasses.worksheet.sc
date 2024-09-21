// Typeclasses!
// The typeclass pattern, originally created in Haskell,
// is a powerful mechanism to achieve ad-hoc polymorphism.
// The main idea behind them is simple:
// "Segregate a type definition, from the implementation of its behaviors".

println("Numeric")

// Remember the sum method we defined in the previous session?
// It was specific to Ints, despite the logic being applicable to any number.
// If we wanted a version for Doubles, Longs, or even a custom fractional number,
// we would need to implement all the logic ourselves again.
// However, we could rather abstract the specifics and write a generic function,
// using the Numeric typeclass.

def genericSum[A](nums: List[A])(using ev: Numeric[A]): A =
  @annotation.tailrec
  def loop(remaining: List[A], acc: A): A =
    remaining match
      case head :: tail =>
        loop(
          remaining = tail,
          ev.plus(acc, head)
        )

      case Nil =>
        acc

  loop(remaining = nums, acc = ev.zero)
end genericSum

genericSum(List(1, 2, 3, 4, 5))
genericSum(List(3.3d, 5.0d, 11.11d))

println("-----")

println("Monoid")

// While Numeric is very useful on its own.
// It is still very restrictive,
// since we actually only need a subset of what a number is:
// - An empty element.
// - A combine operation.
// We could write a even more generic version: combineAll
// And it turns out a Monoid is exactly what we need.
import cats.Monoid

def combineAll[A](elems: List[A])(using ev: Monoid[A]): A =
  @annotation.tailrec
  def loop(remaining: List[A], acc: A): A =
    remaining match
      case head :: tail =>
        loop(
          remaining = tail,
          ev.combine(acc, head)
        )

      case Nil =>
        acc
  end loop
  loop(remaining = elems, acc = ev.empty)
end combineAll

combineAll(List(1, 2, 3, 4, 5))
combineAll(List(3.3d, 5.0d, 11.11d))
combineAll(List("foo", "bar", "baz"))

println("-----")

println("Instances for custom types")

// The power of typeclasses resides in the separation of concerns.
// This means that you can provide instances for your types and for third parties.
// Let's implement our own Fractional type and provide an instance of Monoid for it.
final case class Fraction(
    numerator: Int,
    denominator: Int
):
  override def toString: String =
    s"${numerator}/${denominator}"

given Monoid[Fraction] with
  override val empty: Fraction =
    Fraction(numerator = 0, denominator = 1)

  override def combine(x: Fraction, y: Fraction): Fraction =
    Fraction(
      numerator = (x.numerator * y.denominator) + (x.denominator * y.numerator),
      denominator = x.denominator * y.denominator
    )
end given

combineAll(
  elems = List(
    Fraction(
      numerator = 1,
      denominator = 1
    ),
    Fraction(
      numerator = 2,
      denominator = 3
    ),
    Fraction(
      numerator = 3,
      denominator = 5
    )
  )
)

println("-----")

println("Foldable")

// The previous combineAll method is very generic.
// However, it is still restrictive in the collection type; List.
// Sure, the implementation is using pattern matching and recursion,
// which is very specific to List.
// But, we already learnt that foldLeft abstracts away that process.
// Thus, we could write an even more generic combineAll function
// that works on anything that has a foldLeft method.
// That is exactly what Foldable provides.
import cats.Foldable
import cats.syntax.all.* // Provides the |+| operator.
import scala.collection.immutable.ArraySeq

def genericCombineAll[C[_], A](data: C[A])(using Foldable[C], Monoid[A]): A =
  data.foldLeft(Monoid[A].empty)(_ |+| _)

genericCombineAll(List(1, 2, 3, 4, 5))
genericCombineAll(Vector(3.3d, 5.0d, 11.11d))
genericCombineAll(ArraySeq("foo", "bar", "baz"))

// And, of course, that operation already exists in cats.
List(1, 2, 3, 4, 5).combineAll
Vector(3.3d, 5.0d, 11.11d).combineAll
ArraySeq("foo", "bar", "baz").combineAll

println("-----")

println("Bonus: Group-Map-Reduce")

// The Monoid of Map[K, V] is very powerful.
// Because when combining two maps together,
// if there are duplicate keys it merges the values using their Semigroup.
// This can be used to write a very generic groupMapReduce.
import cats.Semigroup

def genericGroupMapReduce[C[_], A, K, V](
    data: C[A]
)(
    f: A => (K, V)
)(using
    Foldable[C],
    Semigroup[V]
): Map[K, V] =
  data.foldMap(a => Map(f(a)))

genericGroupMapReduce(
  data = List("foo", "bar", "baz", "quax")
) { word =>
  word.head -> Fraction(
    numerator = word.count(l => l == 'a'),
    denominator = word.size
  )
}

println("-----")
