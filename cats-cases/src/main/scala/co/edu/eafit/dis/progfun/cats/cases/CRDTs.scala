package co.edu.eafit.dis.progfun.cats.cases

import cats.Monoid
import cats.instances.int._ // Brings the implicit Monoid[Int] instance to scope.
import cats.instances.list._ // Brings the implicit Monoid[Map] & Traverse[Map[_, _]] instances to scope.
import cats.instances.map._ // Brings the implicit Traveser[List[_]] instance to scope.
import cats.syntax.foldable._ // Provides the combineAll method.
import cats.syntax.semigroup._ // Provides the |+| method.

object CRDTs extends App {
  // Idempotent Commutative Monoid.
  trait BoundedSemiLattice[A] extends Monoid[A] {
    def empty: A
    def combine(a1: A, a2: A): A
  }

  object BoundedSemiLattice {
    implicit val intInstance: BoundedSemiLattice[Int] = new BoundedSemiLattice[Int] {
      override val empty: Int = 0
      override def combine(a1: Int, a2: Int): Int = a1 max a2
    }

    implicit def setInstance[A]: BoundedSemiLattice[Set[A]] = new BoundedSemiLattice[Set[A]] {
      override def empty: Set[A] = Set.empty[A]
      override def combine(a1: Set[A], a2: Set[A]): Set[A] = a1 union a2
    }
  }

  // Type class for GCounter.
  trait GCounter[F[_, _], K, V] {
    def increment(f: F[K, V])(k: K, v: V)(implicit m: Monoid[V]): F[K, V]

    def merge(f1: F[K, V], f2: F[K, V])(implicit b: BoundedSemiLattice[V]): F[K, V]

    def total(f: F[K, V])(implicit m: Monoid[V]): V
  }

  object GCounter {
    implicit def mapInstance[K, V]: GCounter[Map, K, V] = new GCounter[Map, K, V] {
      override def increment(map: Map[K, V])(id: K, amount: V)(implicit m: Monoid[V]): Map[K, V] =
        map.updated(key = id, value = amount |+| map.getOrElse(id, m.empty))

      override def merge(map1: Map[K, V], map2: Map[K, V])(implicit b: BoundedSemiLattice[V]): Map[K, V] =
        map1 |+| map2

      override def total(map: Map[K, V])(implicit m: Monoid[V]): V =
        map.valuesIterator.reduce(_ |+| _)
    }
  }

  // Type class for Key - Value store.
  trait KeyValueStore[F[_, _]] {
    def get[K, V](f: F[K, V])(k: K): Option[V]

    final def getOrElse[K, V](f: F[K, V])(k: K, default: V): V =
      get(f)(k).getOrElse(default)

    def put[K, V](f: F[K, V])(k: K, v: V): F[K, V]

    def values[K, V](f: F[K, V]): List[V]
  }

  object KeyValueStore {
    implicit val keyValueMapInstance: KeyValueStore[Map] = new KeyValueStore[Map] {
      override def put[K, V](m: Map[K, V])(k: K, v: V): Map[K, V] =
        m + (k -> v)

      override def get[K, V](m: Map[K, V])(k: K): Option[V] =
        m.get(k)

      override def values[K, V](f: Map[K, V]): List[V] =
        f.valuesIterator.toList
    }

    implicit def gcounterInstance[F[_, _], K, V](
        implicit kvs: KeyValueStore[F],
        km: Monoid[F[K, V]]
    ): GCounter[F, K, V] = new GCounter[F, K, V] {
      def increment(f: F[K, V])(key: K, value: V)(implicit m: Monoid[V]): F[K, V] = {
        val total = f.getOrElse(key, m.empty) |+| value
        f.put(key, total)
      }

      def merge(f1: F[K, V], f2: F[K, V])(implicit b: BoundedSemiLattice[V]): F[K, V] =
        f1 |+| f2

      def total(f: F[K, V])(implicit m: Monoid[V]): V =
        f.values.combineAll
    }
  }

  // Syntax for any data type of KeyValueStore.
  implicit class KvsOps[F[_, _], K, V](private val f: F[K, V]) extends AnyVal {
    def put(key: K, value: V)(implicit kvs: KeyValueStore[F]): F[K, V] =
      kvs.put(f)(key, value)

    def get(key: K)(implicit kvs: KeyValueStore[F]): Option[V] =
      kvs.get(f)(key)

    def getOrElse(key: K, default: V)(implicit kvs: KeyValueStore[F]): V =
      kvs.getOrElse(f)(key, default)

    def values(implicit kvs: KeyValueStore[F]): List[V] =
      kvs.values(f)
  }

  // Test.
  val map1 = Map("a" -> 7, "b" -> 3)
  val map2 = Map("a" -> 2, "b" -> 5)
  val gcounter = implicitly[GCounter[Map, String, Int]]
  val merged = gcounter.merge(map1, map2)
  val totalized = gcounter.total(merged)
  println(
    s"""Given
       |  map1 = Map('a' -> 7, 'b' -> 3) & map2 = Map('a' -> 2, 'b' -> 5)
       |Then:
       |  merge(map1, map2) = ${merged}
       |  total(merge(map1, map2)) = ${totalized}
    """.stripMargin
  )
}
