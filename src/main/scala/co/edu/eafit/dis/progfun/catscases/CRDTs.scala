package co.edu.eafit.dis.progfun.catscases

import cats.Monoid
import cats.syntax.semigroup._ // for |+|
import cats.instances.map._ // for Monoid
import cats.instances.list._ // for Monoid
import scala.language.higherKinds // For F[_]
import cats.syntax.foldable._ // for combineAll


// Commutative Replicated Data Types
object CRDTs extends App {

  final case class _GCounter(counters: Map[String, Int]) {
    def increment(machine: String, amount: Int): _GCounter = {
      val value = amount + counters.getOrElse(machine, 0)
      new _GCounter(counters + (machine -> value))
    }

    def merge(that: _GCounter): _GCounter =
      new _GCounter(that.counters ++ this.counters.map {
        case(k, v) =>
          k -> (v max that.counters.getOrElse(k, 0))
      })

    def total: Int =
      counters.values.foldLeft(0)(_ + _)
  }

  //BoundedSemiLattice aka. Idempotent Commutative Monoid
  trait BoundedSemiLattice[A] extends Monoid[A] {
    def combine(a1: A, a2: A): A

    def empty: A
  }

  object BoundedSemiLattice {
    implicit val intInstance: BoundedSemiLattice[Int] =
      new BoundedSemiLattice[Int] {
        override def combine(a1: Int, a2: Int): Int = a1 max a2

        override def empty: Int = 0
      }

    implicit def setInstance[A]: BoundedSemiLattice[Set[A]] =
      new BoundedSemiLattice[Set[A]] {
        override def combine(a1: Set[A], a2: Set[A]): Set[A] = a1 union a2

        override def empty: Set[A] = Set.empty[A]
      }
  }

  final case class GCounterA[A](counters: Map[String, A]) {

    def increment(machine: String, amount: A)
         (implicit boundedSemiLattice: BoundedSemiLattice[A]): GCounterA[A] = {
      val value =
        amount |+| counters.getOrElse(machine, boundedSemiLattice.empty)

      new GCounterA(counters + (machine -> value))
    }

    def merge(that: GCounterA[A])
            (implicit boundedSemiLattice: BoundedSemiLattice[A]): GCounterA[A] =
      new GCounterA(this.counters |+| that.counters)

    def total(implicit boundedSemiLattice: BoundedSemiLattice[A]): A =
      counters.values.foldLeft(boundedSemiLattice.empty)(_ |+| _)
  }


  // Type class of a GCounter
  trait GCounter[F[_, _], K, V] {
    def increment(f: F[K, V]) (k: K, v: V)
                 (implicit m: Monoid[V]): F[K, V]

    def merge(f1: F[K, V], f2: F[K, V])
             (implicit b: BoundedSemiLattice[V]): F[K, V]

    def total(f: F[K, V])
             (implicit m: Monoid[V]): V
  }

  object GCounter {
    def apply[F[_, _], K, V]
    (implicit counter: GCounter[F, K, V]) =
      counter
  }


  // Type class instance
  implicit def mapInstance[K, V]: GCounter[Map, K, V] =
    new GCounter[Map, K, V] {
      override def increment(map: Map[K, V])(id: K, amount: V)
                            (implicit m: Monoid[V]): Map[K, V] = {

        val value = amount |+| map.getOrElse(id, m.empty)
        map + (id -> value)
      }

      override def merge(map1: Map[K, V], map2: Map[K, V])
                        (implicit b: BoundedSemiLattice[V]): Map[K, V] =
        map1 |+| map2

      override def total(map: Map[K, V])(implicit m: Monoid[V]): V =
        map.values.toList.combineAll
    }

  // Test
  import cats.instances.int._ // for Monoid
  val g1 = Map("a" -> 7, "b" -> 3)
  val g2 = Map("a" -> 2, "b" -> 5)
  val counter = GCounter[Map, String, Int]
  val merged = counter.merge(g1, g2)
  println(merged.toString())
  // merged: Map[String,Int] = Map(a -> 7, b -> 5)
  val total = counter.total(merged)
  // total: Int = 12
  println(total)


  // Type class for Key - Value
  trait KeyValueStore[F[_,_]] {
    def put[K, V](f: F[K, V])(k: K, v: V): F[K, V]

    def get[K, V](f: F[K, V])(k: K): Option[V]

    def getOrElse[K, V](f: F[K, V])(k: K, default: V): V =
      get(f)(k).getOrElse(default)

    def values[K, V](f: F[K, V]): List[V]
  }

  implicit val keyValueMapInstance: KeyValueStore[Map] =
    new KeyValueStore[Map] {
      override def put[K, V](f: Map[K, V])(k: K, v: V): Map[K, V] =
        f + (k -> v)

      override def get[K, V](f: Map[K, V])(k: K): Option[V] =
        f.get(k)

      override def getOrElse[K, V](f: Map[K, V])(k: K, default: V): V =
        super.getOrElse(f)(k, default)

      override def values[K, V](f: Map[K, V]): List[V] =
        f.values.toList
    }

  implicit class KvsOps[F[_,_], K, V](f: F[K, V]) {
    def put(key: K, value: V)
           (implicit kvs: KeyValueStore[F]): F[K, V] =
      kvs.put(f)(key, value)
    def get(key: K)(implicit kvs: KeyValueStore[F]): Option[V] =
      kvs.get(f)(key)
    def getOrElse(key: K, default: V)
                 (implicit kvs: KeyValueStore[F]): V =
      kvs.getOrElse(f)(key, default)
    def values(implicit kvs: KeyValueStore[F]): List[V] =
      kvs.values(f)
  }

  implicit def gcounterInstance[F[_,_], K, V]
  (implicit kvs: KeyValueStore[F], km: Monoid[F[K, V]]) =
    new GCounter[F, K, V] {
      def increment(f: F[K, V])(key: K, value: V)
                   (implicit m: Monoid[V]): F[K, V] = {
        val total = f.getOrElse(key, m.empty) |+| value
        f.put(key, total)
      }
      def merge(f1: F[K, V], f2: F[K, V])
               (implicit b: BoundedSemiLattice[V]): F[K, V] =
        f1 |+| f2
      def total(f: F[K, V])(implicit m: Monoid[V]): V =
        f.values.combineAll
    }

  // Test
  import cats.instances.int._ // for Monoid
  val map1 = Map("a" -> 7, "b" -> 3)
  val map2 = Map("a" -> 2, "b" -> 5)
  val gcounter = gcounterInstance[Map, String, Int]
  val merge = counter.merge(map1, map2)
  println(merged.toString())
  // merged: Map[String,Int] = Map(a -> 7, b -> 5)
  val totalize = counter.total(merged)
  // total: Int = 12
  println(totalize)
}