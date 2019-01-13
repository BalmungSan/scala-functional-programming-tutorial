package co.edu.eafit.dis.progfun.scalaintro

import scala.annotation.tailrec

object ScalaNotes extends App {
  // This a very brief introduction to the basic elements of the language.
  // For a more detailed and complete introduction refer to the "Tour of Scala".
  println("- Scala Introduction! -")
  println("Hello, World! - Traditions matter!")
  println()

  // Values & Declarations!
  // When declaring a new value in you program, scala provides you
  // with four alternatives: val, var, def & lazy val.
  // They all differ in being:
  //   - mutable / immutable.
  //   - eager   / lazy.
  //   - cached  / non-cached.
  // VAL: A val is an immutable eager cached value.
  //      Once defined it will computes and stores
  //      its value, which can not be modified latter.
  // VAR: A var is a mutable eager cached value.
  //      A val whose value can be reassigned multiple times,
  //      however they all have to be of the same type.
  // DEF: A definition (def) is an immutable lazy non-cached value.
  //      Its value will be computed each time it is called.
  //      A definition may take arguments, which make them ideal for
  //      defining methods.
  // LAZY VAL: A lazy val is an immutable lazy cached value.
  //           Its value computation will be delayed until its first access,
  //           and will store it & return it on every other access.
  println("-- VAL - VAR - DEF - LAZY VAL --")
  println("--- VAL ---")
  val value1 = { println("Inside VAL definition."); 0 }
  println("VAL defined")
  println(s"VAL first access: ${value1}.")
  println(s"VAL second access: ${value1}.")
  println("--- VAR ---")
  var value2 = { println("Inside VAR definition."); 1 }
  println("VAR defined")
  value2 = { println("Insie VAR reassignment."); 2 }
  println(s"VAR first access: ${value2}.")
  println(s"VAR second access: ${value2}.")
  println("--- DEF ---")
  def value3 = { println("Inside DEF definition."); 3 }
  println("DEF defined")
  println(s"DEF first access: ${value3}.")
  println(s"DEF second access: ${value3}.")
  println("--- LAZY VAL ---")
  lazy val value4 = { println("Inside LAZY VAL definition."); 5 }
  println("LAZY VAL defined")
  println(s"LAZY VAL first access: ${value4}.")
  println(s"LAZY VAL second access: ${value4}.")
  println()

  // Functions & Methods!
  // Functions and Methods are expressions that take parameters,
  // and return one value - the last expression in the body
  // is the return value of the function.
  // Note: Even if functions and methods seems very similar,
  // They are not the same thing!
  // For more information, please refer to the Daniel's great answer
  // to the 'Difference between method and function in Scala'
  // StackOverflow post.
  // Method definition.
  println("-- Functions & Methods --")
  def method(x: Int): Int = x + 1
  println(s"Given method(x: Int): Int = x + 1\t->\tmethod(3) = ${method(3)}")
  // Anonymous function.
  val anonymous = (x: Int, y: Int) => x + y
  println(s"Given anonymous = (x: Int, y: Int) => x + y\t->\tanonymous(3, 5) = ${anonymous(3, 5)}")
  // Higher order function.
  def higher(x: Int, f: Int => Int): Int = f(x + 1)
  println(s"Given higher(x: Int, f: Int => Int): Int = f(x + 1)\t->\thiger(3, x => x) = ${higher(3, x => x)}")
  // Curryfied function.
  def curryfied(x: Int)(y: Int): Int = x + y
  val add3: Int => Int = curryfied(3)
  println(s"Given curryfied(x: Int)(y: Int): Int = x + y & add3 = curryfied(3)\t->\tadd3(5) = ${add3(5)}")
  // Recursive function.
  def fact(n: Long): Long =
    if (n == 0) 0 else n * fact(n - 1)
  println(s"Given fact(n: Long): Long = if (n == 0) 0 else n * fact(n - 1)\t->\tfact(5) = ${fact(5)}")
  // Tail-recursive function.
  def tailFact(n: Long) = {
    @tailrec
    def loop(n: Long, acc: Long): Long =
      if (n == 0) acc else loop(n - 1, acc * n)
    loop(n, 1)
  }
  println(s"Given tailFact(n: Long): Long = loop(n, 1) & def loop(n: Long, acc: Long): Long = if (n == 0) acc else loop(n - 1, acc * n)\t->\t tailFact(50) = ${tailFact(50)}")
  // Varargs parameter function.
  def sumAll(nums: Int*): Int = nums.sum
  println(s"Given sumAll(nums: Int*): Int = nums.sum\t->\tsumAll(1, 2, 3) = ${sumAll(1, 2, 3)}")
  // By-Name parameter function.
  def runOnlyIf(messageBlock: => String, run: Boolean = true): String =
    if (run)
      messageBlock
    else
      "Not execute!"
  def executed = runOnlyIf({ println("Executed"); "Hello, World!" })
  def notExecuted = runOnlyIf({ println("Executed"); "Hello, World!" }, run = false)
  println("Given runOnlyIf(messageBlock: => String, run: Boolean = true): String = if (run) messageBlock else 'Not execute!'")
  println(s"\trunOnlyIf({ println('Executed'); 'Hello, World!' }) = ${executed}")
  println(s"\trunOnlyIf({ println('Executed'); 'Hello, World!' }, run = false) = ${notExecuted}")
  println()

  // Collections!
  // Collections are data structures that abstract away multiplicity.
  // One can easily argue that almost every programming project
  // has to use them. For that reason, modern programming languages
  // include a collection framework in their core library.
  // In Scala there are four main categories of collections,
  // Immutable / Mutable & Serial / Parallel.
  // We will look only to the three most common of
  // the immutable-serial group: Lists, Sets & Maps.
  println("-- Collections --")
  println("--- List - Set - Map ---")
  // List - Ordered collection of elements of the same type.
  //        You can efficiently traverse it in a head-tail fashion.
  val list = List(1, 2, 3)
  println(s"List(1, 2, 3) = ${list}")
  println(s"Given list = List(1, 2, 3)\t->\tlist.head = ${list.head} & list.tail = ${list.tail}")
  // Set - Unordered collection of unique elements of the same type.
  //       You can use them to efficiently check if a value is contained.
  val set = Set(1, 2, 2, 3, 3, 3, 4, 5)
  println(s"Set(1, 2, 2, 3, 3, 3, 4, 5) = ${set}")
  println(s"Given set = Set(1, 2, 3, 4, 5)\t->\tset.contains(3) = ${set.contains(3)} & set.contains(0) = ${set.contains(0)}")
  // Map - Unordered collection of key-value pairs,
  //       All keys have to be of the same type and are unique.
  //       All values have to be of the same type,
  //       but can be a different type from the keys.
  //       You can efficiently access a value given the key.
  val map = Map('a' -> 1, 'e' -> 2, 'i' -> 3, 'o' -> 4, 'u' -> 5)
  println(s"Map('a' -> 1, 'e' -> 2, 'i' -> 3, 'o' -> 4, 'u' -> 5) = ${map}")
  println(s"Given map = Map('a' -> 1, 'e' -> 2, 'i' -> 3, 'o' -> 4, 'u' -> 5)\t->\tmap.get('a') = ${map.get('a')} & map.get('b') = ${map.get('b')}")
  // Common methods on collections!
  // Note that all this methods return a new collection
  // with the applied transformation, instead of mutating
  // the original collection.
  //
  // reverse (list only) - reverse the elements of a list.
  println("--- Common collection's methods ---")
  val reversed = List(1, 2, 3).reverse
  println(s"List(1, 2, 3).reverse = ${reversed}")
  // sort (list only) - sorts the elements of a list.
  val sortedAuto = List(2, 3, 1).sorted // The default sorting is ascending.
  println(s"List(2, 3, 1).sorted = ${sortedAuto}")
  val sortedManual = List(2, 3, 1).sortWith(_ > _) // Custom descending sort.
  println(s"List(2, 3, 1).sortBy(_ > _) = ${sortedManual}")
  // zip (list only) joins two lists together into a list of pairs.
  val zipped = List(1, 2, 3) zip List('a', 'b', 'c')
  println(s"List(1, 2, 3) zip List('a', 'b', 'c') = ${zipped}")
  // unzip (list only) - divides a list of pairs into two lists.
  val (nums, letters) = zipped.unzip
  println(s"List((1,a), (2,b), (3,c)).unzip = ${nums} & ${letters}")
  // map - applies a function to all elements of the collection.
  val mappedList = List(1, 2, 3).map(x => x + 1)
  println(s"List(1, 2, 3).map(x => x + 1) = ${mappedList}")
  val mappedSet = Set(-2, -1, 1, 2).map(x => x * x)
  println(s"Set(-2, -1, 1, 2).map(x => x * x) = ${mappedSet}")
  val mappedMap = Map('a' -> 1, 'b' -> 2, 'c' -> 3).map {
    case (key, value) => (s"${key}x") -> (value + 1)
  }
  println(s"Map('a' -> 1, 'b' -> 2, 'c' -> 3).map((key, value) => (s'$${key}x') -> (value + 1)) = ${mappedMap}")
  // mapValues (map only) - applies the function only to the values and leaves the same key.
  val mappedValuesMap = Map('a' -> 1, 'b' -> 2, 'c' -> 3).mapValues(x => x + 1)
  println(s"Map('a' -> 1, 'b' -> 2, 'c' -> 3).mapValues(x => x + 1) = ${mappedValuesMap}")
  // filter - removes all elements that don't satisfy a predicate.
  val filteredList = List(1, 2, 3).filter(x => (x % 2) != 0)
  println(s"List(1, 2, 3).filter(x => (x % 2) != 0) = ${filteredList}")
  val filteredSet = Set(1, 2, 3).filter(x => (x % 2) != 0)
  println(s"Set(1, 2, 3).filter(x => (x % 2) != 0) = ${filteredSet}")
  val filteredMap = Map(1 -> 'a', 2 -> 'b', 3 -> '&').filter {
    case (key, value) => ((key % 2) != 0) && (value.isLetter)
  }
  println(s"Map(1 -> 'a', 2 -> 'b', 3 -> '&').filter((key, value) => ((key % 2) != 0) && value.isLetter) = ${filteredMap}")
  // filterKeys (map only) - apply the predicate only to the keys.
  val filteredKeyMap = Map(1 -> 'a', 2 -> 'b', 3 -> 'c').filterKeys(x => (x % 2) != 0)
  println(s"Map(1 -> 'a', 2 -> 'b', 3 -> 'c').filterKeys(x => (x % 2) != 0) = ${filteredKeyMap}")
  // forall - checks if all elements in a collection satisfy a predicate.
  val forall = Set(1, 2, 3).forall(x => (x * x) < 10)
  println(s"Set(1, 2, 3).forall(x => (x * x) < 10) is ${forall}")
  // exists - checks if there is at least one element in a collection that satisfy a predicate.
  val exists = List(1, 2, 3).exists(x => (x * x) > 10)
  println(s"List(1, 2, 3).exists(x => (x * x) > 10) is ${exists}")
  // foldLeft (list only) - reduces the elements of a list by applying a combine function from left to right.
  val reduced = List(1, 2, 3).foldLeft(0)(_ + _)
  println(s"List(1, 2, 3).foldLeft(0)(_ + _) = ${reduced}")
  // sum - shortcut for summing together all elements in a collection.
  val summed = List(1, 2, 3).sum
  println(s"List(1, 2, 3).sum = ${summed}")
  // mkString - formats a collection as a String.
  val formatted = List(1, 2, 3).mkString(start = "[", sep = ", ", end = "]")
  println(s"List(1, 2, 3).mkString('[', ', ', ']') = ${formatted}")
  // For comprehension!
  // It is nice syntax for map, flatMap and filter.
  println("--- For Comprehension ---")
  val distinctTuples = for {
    x <- 0 to 3 //  Desugared as a flatMap.
    y <- 0 to 3 //  Desugared as a flatMap.
    if x != y //    Desugared as a filter.
  } yield (x, y) // Desugared as a map.
  println(s"for (x <- 0 to 3; y <- 0 to 3; if x != y) yield (x, y) = ${distinctTuples.toList}")
  // Note: There are many other collections, including immutable indexed
  //       Vectors and mutable (fast) Arrays, and methods to transform them.
  //       These were only the most basic and common ones.
  println()

  // Classes, Objects & Traits.
  // Classes object and traits allow us to define new types.
  println("-- Classes - Objects - Traits --")
  // Classes.
  // A class is a blueprint for creating objects,
  // they usually consist of fields (data)
  // and methods (behaviors) that represent an entity.
  println("--- Classes ---")
  class Point(val x: Double, val y: Double) {
    def distance(that: Point): Double =
      math.sqrt(math.pow(this.x - that.x, 2) + math.pow(this.y - that.y, 2))
  }
  println(
    """class Point(val x: Double, val y: Double) {
      |   def distance(that: Point): Double =
      |     sqrt(pow(this.x - that.x, 2) + pow(this.y - that.y, 2))
      |}""".stripMargin
  )
  val p1 = new Point(x = 0, y = 0)
  val p2 = new Point(x = 3, y = 5)
  val distance = p1 distance p2
  println(s"Given p1 = new Point(0, 0) & p2 = new Point(3, 5)\t->\tp1 distance p2 = ${distance}")
  // Objects.
  // Objects are a shortcut for implementing the Singleton pattern
  // They are like classes with only one instance, theyself.
  println("--- Objects ---")
  object NumChecker {
    def isOdd(x: Int): Boolean =
      (x % 2) != 0

    def isEven(x: Int): Boolean =
      !isOdd(x)
  }
  println(
    """object NumChecker {
      |  def isOdd(x: Int): Boolean =
      |    (x % 2) != 0
      |  def isEven(x: Int): Boolean =
      |    !isOdd(x)
      |}""".stripMargin
  )
  val is5Odd = NumChecker.isOdd(5)
  val is5Even = NumChecker.isEven(5)
  println(s"NumChecker.isOdd(5) is ${is5Odd} & NumChecker.isEven(5) is ${is5Even}")
  // Note: Objects are usually used as companions of classes,
  //       In this case there are used to define method related to the class,
  //       but that behave the same no matter the concrete instance
  //       or that should not require any instance.
  //       (Like a static method in Java).
  //       In order to made son, the object must have the same name of the class
  //       and be defined in the same compilation unit (same file).
  //
  // Traits.
  // Traits are abstract types that define which fields and methods
  // can be called on instances of sub-types of them.
  // They can be extended by any class, object or other traits,
  // and multiple traits can be combined when extending them.
  // They can not have parameters (constructor).
  // They may provide default implementations for their fields and methods.
  // (They are similar to Java 8 interfaces with default implementations).
  println("--- Traits  ---")
  trait Iterator[T] {
    def hasNext: Boolean
    def next(): Option[T]
  }
  final class IntIterator(to: Int) extends Iterator[Int] {
    private[this] var current = 0
    override def hasNext: Boolean = current <= to
    override def next(): Option[Int] =
      if (hasNext) {
        val result = Some(current)
        current += 1
        result
      } else {
        None
      }
  }
  println(
    """trait Iterator[T] {
      |  def hasNext: Boolean
      |  def next(): Option[T]
      |}
      |final class IntIterator(to: Int) extends Iterator[Int] {
      |  private[this] var current = 0
      |  override def hasNext: Boolean = current <= to
      |  override def next(): Option[Int] =
      |    if (hasNext) {
      |      val result = Some(current)
      |      current += 1
      |      result
      |    } else {
      |      None
      |    }
      |}""".stripMargin
  )
  val toOne = new IntIterator(to = 1)
  val firstIteration = toOne.next()
  val secondIteration = toOne.next()
  val thirdIteration = toOne.next()
  println(s"Given toOne = new IntIterator(to = 1)\t->\t toOne.next() = ${firstIteration} & toOne.next() = ${secondIteration} & toOne.next() = ${thirdIteration}")
  println()

  // Pattern matching!
  // Pattern matching is a mechanism for checking a value against a pattern.
  // A successful match can also deconstruct a value into its constituent parts.
  // (It is a more powerful version of the switch statement in Java).
  // Note: The following example is only to show all things
  //       you can do on a pattern matching,
  //       but you should never write a function that accepts an Any.
  println("-- Pattern matching --")
  final val Constant: Int = 10
  def patternMatch(x: Any, y: Any = 0): String = x match {
    case 0                      => "Int Zero" // Simple value match.
    case Constant               => "Int 10" // Constant match.
    case `y`                    => "Y" // Variable match.
    case boolean: Boolean       => "Boolean" // Type match.
    case int: Int if (int > 10) => "Int greater than ten" // Guard match.
    case x :: xs                => s"List with head: ${x} & tail: ${xs}" // Structure match - List.
    case (x, y, z)              => s"Tuple3: (${x}, ${y}, ${z})" // Structure match - Tuples.
    case _                      => "Other thing" // Default match.
  }
  println(
    """def patternMatch(x: Any, y: Any = 0): String = x match {
      |  case 0                      => 'Int Zero' // Simple value match.
      |  case Constant               => 'Int 10' // Constant match.
      |  case `y`                    => 'Y' // Variable match.
      |  case boolean: Boolean       => 'Boolean' // Type match.
      |  case int: Int if (int > 10) => 'Int greater than ten' // Guard match.
      |  case x :: xs                => s'List with head: ${x} & tail: ${xs}' // Structure match.
      |  case (x, y, z)              => s'Tuple3: (${x}, ${y}, ${z})' // Structure match - Tuples.
      |  case _                      => 'Other thing' // Default match.
      |}""".stripMargin
  )
  val match1 = patternMatch(0)
  println(s"patternMatch(0) = ${match1}")
  val match2 = patternMatch(10)
  println(s"patternMatch(10) = ${match2}")
  val match3 = patternMatch(x = 5, y = 5)
  println(s"patternMatch(x = 5, y = 5) = ${match3}")
  val match4 = patternMatch(true)
  println(s"patternMatch(true) = ${match4}")
  val match5 = patternMatch(100)
  println(s"patternMatch(100) = ${match5}")
  val match6 = patternMatch(List(1, 2, 3))
  println(s"patternMatch(List(1, 2, 3)) = ${match6}")
  val match7 = patternMatch((1, 2, 3))
  println(s"patternMatch((1, 2, 3)) = ${match7}")
  val match8 = patternMatch(3)
  println(s"patternMatch(3) = ${match8}")
  // You can also pattern match agains regexp
  // For more information read:
  //     https://www.scala-lang.org/api/current/scala/util/matching/Regex.html
  //     https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
  println()

  // Case classes & Case objects!
  println("-- Case Classes & Case objects --")
  // Case classes are like regular classes with a few differences
  // which make them perfect for modeling simple and immutable data structures.
  // (They are similar to a POJO, but with a lot of boilerplate reduced)
  println("--- Case classses ---")
  final case class User(name: String, age: Int)
  println("final case class User(name: String, age: Int)")
  // This one-line-declaration has given us:
  //   1. A new type/class.
  //   2. Public getters for each parameter.
  //   3. A factory constructor - no need to use the new keyword for instantiation.
  //   4. An extractor object - we can use User for pattern matching.
  //   5. A nice string representation.
  //   6. By value comparison.
  //   7. A copy method.
  //   8. Other useful methods
  val user1 = User(name = "BalmungSan", age = 21)
  println(s"Given user1 = User(name = 'BalmungSan', age = 21)\t->\t${user1}")
  println(s"user1.name = ${user1.name}")
  println(s"user1.age = ${user1.age}")
  val copy = user1.copy(age = 10)
  println(s"user1.copy(age = 10) = ${copy}")
  val user2 = User(name = "Luis Miguel", age = 22)
  println(s"Given user2 = ${user2}\t->\tuser1 == user2 is ${user1 == user2}")
  val user3 = User(name = "BalmungSan", age = 21)
  println(s"Given user3 = ${user3}\t->\tuser1 == user2 is ${user1 == user3}")
  def getUserAge(user: User): Int = user match {
    case User(name, age) => age
  }
  println(s"Given getUserAge(user: User): Int = user match { case User(_, age) => age }\t->\tgetUserAge(user1) = ${getUserAge(user1)}")
  // Case objects and ADTs
  // A case object is a used to ensure only one instance
  // of a case class with no arguments.
  // Algebraic Data Types are types formed by combining other types.
  // In Scala, ADTs are usually encoded using sealed traits and case classes/objects.
  // A sealed trait is a trait who can only be extended in the same file.
  // This allow us to have exhaustive pattern matching.
  // For showing this, lets define our own Option type,
  // which we will call Maybe (in honor to the Haskell one).
  println("--- Case objects & ADTs ---")
  sealed trait Maybe[+T] {
    final def getOrElse[S >: T](default: => S): S = this match {
      case Just(t) => t
      case Nothing => default
    }
  }
  final case class Just[T](value: T) extends Maybe[T]
  case object Nothing extends Maybe[Nothing]
  println(
    """sealed trait Maybe[+T] {
      |  final def getOrElse[S >: T](default: => S): S = this match {
      |    case Just(t) => t
      |    case Nothing => default
      |  }
      |}
      |final case class Just[T](value: T) extends Maybe[T]
      |case object Nothing extends Maybe[Nothing]""".stripMargin
  )
  val maybe1: Maybe[Int] = Just(10)
  val get1 = maybe1.getOrElse(default = 0)
  println(s"Given maybe1 = Just(10)\t->\tmaybe1.getOrElse(0) = ${get1}")
  val maybe2: Maybe[Int] = Nothing
  val get2 = maybe2.getOrElse(default = 0)
  println(s"Given maybe2 = Nothing\t->\tmaybe2.getOrElse(0) = ${get2}")
}
