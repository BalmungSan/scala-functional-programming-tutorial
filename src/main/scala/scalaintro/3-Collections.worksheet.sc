import util.Try

// Scala has a very rich standard library,
// specially its collections module.
// Most common collections are already imported by default,
// but more specialized ones can be found in the
// scala.collections.immutable package.

println("List")

// Single-linked Lists are a very basic data structure,
// which are very common to FP languages,
// because they are very useful for writing recursive algorithms.
// They are good enough for full traversal operations,
// but are very inefficient for "common" imperative operations
// like accessing elements by index or requesting the size of the List.
val list = List(1, 2, 3)

// They have by-value equality.
list == List(1, 2, 3)
list == List(1, 3, 2)

// And human-readable default string representation.
println(list)

// These operations require traversing the list,
// meaning they are O(n) rather than O(1).
// Access by index (unsafe).
list(2)
Try(list(3))
// Access by index (safe).
list.lift(2)
list.lift(3)
// Size.
list.length
// Appending.
list :+ 4

// But, prepending is very efficient O(1).
0 :: list

// Head and tail access are also very efficient O(1).
list.head
list.tail

println("-----")

println("Custom List from scratch")

// Implementing a custom List from scratch is very easy.
// A List is essence a simple ADT.
// It is either empty, or it is non-empty.
// If non-empty it has an element as its head,
// Meaning a List is a succession of non-empty lists
// until it reaches the empty one.
// and another list as its tail.
enum MyList[+A]:
  case Empty
  case NonEmpty(head: A, tail: MyList[A])

// These two expressions are equivalent,
// they both create a List composed of the numbers 1, 2, 3
// The :: is the NonEmpty case; known as Cons.
// And Nil is the empty case.
val l = 1 :: 2 :: 3 :: Nil
l == List(1, 2, 3)

// This explains why prepending is fast but appending is not.
// For prepending you can reuse the previous List.
// But, for appending, you need to traverse the List,
// add the new element before the end, and re-create everything.
0 :: l
l :+ 4

// We can see that more easily using our custom List.
val ml = MyList.NonEmpty(
  head = 1,
  tail = MyList.NonEmpty(
    head = 2,
    tail = MyList.NonEmpty(
      head = 3,
      tail = MyList.Empty
    )
  )
)

// Prepending is literally only creating a new NonEmpty node.
MyList.NonEmpty(head = 0, tail = ml)

// But appending requires a full traversal and re-creation.
def append[A](ml: MyList[A], a: A): MyList[A] =
  ml match
    case MyList.Empty =>
      MyList.NonEmpty(head = a, tail = MyList.Empty)

    case MyList.NonEmpty(head, tail) =>
      MyList.NonEmpty(head, tail = append(tail, a))
end append
append(ml, 4)

println("-----")

println("Array")

// Like most languages, Scala has arrays.
// However, these are rarely used because the following reasons:
// * They are mutable; in contents not in size.
// * They use reference equality rather than by-value equality.
// * They don't have a human-readable default string representation.
// * They are invariant, and they are not really part of the collections framework.
val arr = Array(1, 2, 3)

// Lack of by-value equality.
arr == Array(1, 2, 3)
arr == arr

// Lack of default human-readable-representation.
println(arr)
// But mkString can be used print their contents.
arr.mkString("[", ", ", "]")

// They can be mutated.
arr(2)
arr(2) = 5
arr(2)

// But, these operations are very efficient O(1).
// Access by index (unsafe).
arr(2)
Try(arr(3))
// Access by index (safe).
arr.lift(2)
arr.lift(3)
// Size.
arr.length

// However, both prepend and append are very inefficient O(N).
// Because they must copy all the data.
arr :+ 4
0 +: arr

println("-----")

println("ArraySeq")

// The stdlib provides a collection called ArraySeq,
// which is a small wrapper over plain arrays
// But who fixes some of downsides of regular arrays like:
// * Making them immutable.
// * Providing by-value equality.
// * Having a human-readable default string representation.
// * They are covariant.
import scala.collection.immutable.ArraySeq
val as = ArraySeq(1, 2, 3)
as == ArraySeq(1, 2, 3)
println(as)

// You can use from to create an ArraySeq from an existing Array,
// this slow but safe since it copies the contents.
// You can also use unsafeWrapArray which doesn't copy,
// but may allow for the underlying Array to be mutated and thus breaking it.
// Similarly, you can use toArray to convert an ArraySeq into an Array,
// which is slow because it requires copying.
// Or you can retrieve the underlying Array.
val copied = ArraySeq.from(arr)
val wrapped = ArraySeq.unsafeWrapArray(arr)
copied == wrapped
copied.toArray
copied.unsafeArray == wrapped.unsafeArray
wrapped.unsafeArray == arr

println("-----")

println("Vector")

// Vectors are very sophisticated data structures.
// Internally, they are trees of Arrays,
// which allow them to be very efficient in most operations.
// Usually there will be a collection that will be better
// for a specific operation, but it will be worse for others.
// Meaning Vectors are a pretty good default collection.
val vec = Vector(1, 2, 3)

// By-value equality.
vec == Vector(1, 2, 3)

// All these operations are roughly O(1) most of the time.
vec(2)
Try(vec(3))
vec.lift(2)
vec.lift(3)
vec.length
vec :+ 4
0 +: vec

println("-----")

println("Set")

// Sets are another kind of collections.
// They are unordered and don't contain duplicates.
// Thus, it doesn't make sense to request elements by index.
// Rather, they are pretty good at contains checks.
val set = Set(1, 2, 3)

// Lack of order.
Set(1, 2, 3, 4, 5, 6, 7)

// Removal of duplicates.
Set(3, 2, 1, 1, 1, 2, 2, 3)

// Comparison by-value ignores order and duplicates.
set == Set(3, 2, 1)
set == Set(3, 2, 1, 1, 1, 2, 2, 3, 3, 1)

// Contains check are their main operation.
set.contains(1)
set.contains(0)

// They are also efficient at telling their size and adding / removing elements.
set.size
set + 5
set - 1

println("-----")

println("Map")

// Maps, similar to Sets are unordered, and their keys are unique.
// They excel at giving you the value associated to a key.
val map = Map(1 -> 'A', 2 -> 'B', 3 -> 'C')

// Comparison by-value ignores order.
map == Map(3 -> 'C', 2 -> 'B', 1 -> 'A')

// Getting value by key (unsafe).
Try(map(key = 1))
Try(map(key = 0))
// Getting value by key (safe).
map.get(key = 1)
map.get(key = 0)

// They also provide key contains check.
// Or getting all keys as a Set.
map.contains(key = 1)
map.keySet

// They are also efficient at telling their size and adding / removing elements.
map.size
map + (4 -> 'D')
map ++ Map(4 -> 'D', 5 -> 'E')
map - 1

println("-----")

println("Super types")

// List and Vectors are very similar.
// If you forget about their performance characteristics,
// their APIs are the same.
// That is because both implement a trait called Seq,
// which defines what a sequence-like collection should provide.
var seq: Seq[Int] = list
seq = vec
seq

// However, Arrays themselves are not Sequences.
// But, an ArraySeq is a Seq.
// seq = arr // This doesn't compile.
seq = as // But this does.
seq

// There are other collections that are also Sequences,
// and som others that are not Sequences but rather Iterables.
seq: Iterable[Int]
set: Iterable[Int]
// Maps are iterables of tuples.
map: Iterable[(Int, Char)]

// These super types can be useful to define very generic operations.
// However, they also hide important details from the underlying types.
// Thus, they should be used with care, otherwise performance degrade a lot.

println("-----")
