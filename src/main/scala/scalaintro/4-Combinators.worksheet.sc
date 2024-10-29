// Scala standard library not only has many useful collection types.
// But also, these provide multiple useful combinators to manipulate them.
// We will see some of the most common ones, applied to realistic use cases;
// as well as implement them from scratch.

println("sum")

// Use case:
// Given a list of ages, return the average age.
val ages = List(27, 30, 45, 18)

// High level implementation.
def averageAge(ages: List[Int]): Int =
  ages.sum / ages.size

averageAge(ages)

// Mutable implementation.
def averageAgeMutable(ages: Array[Int]): Int =
  var sum = 0
  var i = 0
  while i < ages.length do
    sum += ages(i)
    i += 1
  end while
  sum / ages.length
end averageAgeMutable

averageAgeMutable(ages.toArray)

// Recursive implementation.
def sumRecursive(nums: List[Int]): Int =
  nums match
    case Nil =>
      0

    case head :: tail =>
      head + sumRecursive(tail)
end sumRecursive

def averageAgeRecursive(ages: List[Int]): Int =
  sumTailRecursive(ages) / ages.size

averageAgeRecursive(ages)

// Tail-recursive implementation.
def sumTailRecursive(nums: List[Int]): Int =
  @annotation.tailrec
  def loop(remaining: List[Int], acc: Int): Int =
    remaining match
      case head :: tail =>
        loop(
          remaining = tail,
          acc + head
        )

      case Nil =>
        acc

  loop(remaining = nums, acc = 0)
end sumTailRecursive

def averageAgeTailRecursive(ages: List[Int]): Int =
  sumTailRecursive(ages) / ages.size

averageAgeTailRecursive(ages)

println("-----")

println("contains, exist, forall, find, filter")

// Use case:
// Simple data base.
val data = List("foo", "bar", "baz", "quax", "12345", "   ")

// Contains:
// Checks if the list contains an specific element.
data.contains("baz")
data.contains("Luis")

// Recursive implementation.
def containsRecursive[A](data: List[A])(elem: A): Boolean =
  data match
    case Nil =>
      false

    case head :: tail =>
      if head == elem then true
      else containsRecursive(tail)(elem)
end containsRecursive

containsRecursive(data)(elem = "baz")
containsRecursive(data)(elem = "Luis")

// Tail-recursive implementation.
def containsTailRecursive[A](data: List[A])(elem: A): Boolean =
  @annotation.tailrec
  def loop(remaining: List[A]): Boolean =
    remaining match
      case head :: tail =>
        (head == elem) || loop(remaining = tail)

      case Nil =>
        false

  loop(remaining = data)
end containsTailRecursive

containsTailRecursive(data)(elem = "baz")
containsTailRecursive(data)(elem = "Luis")

// Exist:
// Checks if the list contains at least one element that satisfies the predicate.
data.exists(_.startsWith("1"))
data.exists(_.startsWith("0"))

// Tail-recursive implementation.
def existsTailRecursive[A](data: List[A])(predicate: A => Boolean): Boolean =
  @annotation.tailrec
  def loop(remaining: List[A]): Boolean =
    remaining match
      case head :: tail =>
        predicate(head) || loop(remaining = tail)

      case Nil =>
        false

  loop(remaining = data)
end existsTailRecursive

existsTailRecursive(data)(_.startsWith("1"))
existsTailRecursive(data)(_.startsWith("0"))

// Relationship between exists & contains.
def containsViaExists[A](data: List[A])(elem: A): Boolean =
  existsTailRecursive(data)(_ == elem)

containsViaExists(data)(elem = "baz")
containsViaExists(data)(elem = "Luis")

// Forall:
// Checks if all elements satisfy the predicate.
data.forall(_.nonEmpty)
data.forall(_.size > 5)

def forallTailRecursive[A](data: List[A])(predicate: A => Boolean): Boolean =
  @annotation.tailrec
  def loop(remaining: List[A]): Boolean =
    remaining match
      case head :: tail =>
        predicate(head) && loop(remaining = tail)

      case Nil =>
        true

  loop(remaining = data)
end forallTailRecursive

forallTailRecursive(data)(_.nonEmpty)
forallTailRecursive(data)(_.size > 5)

// Equivalence between exists & forall.
def forallViaExists[A](data: List[A])(predicate: A => Boolean): Boolean =
  !data.exists(a => !predicate(a))

forallViaExists(data)(_.nonEmpty)
forallViaExists(data)(_.size > 5)

def existsViaForall[A](data: List[A])(predicate: A => Boolean): Boolean =
  !data.forall(a => !predicate(a))

existsViaForall(data)(_.startsWith("1"))
existsViaForall(data)(_.startsWith("0"))

// Find:
// Returns the first element that satisfies a predicate.
data.find(_.isBlank)
data.find(_.isEmpty)

// Tail-recursive implementation.
def findTailRecursive[A](data: List[A])(predicate: A => Boolean): Option[A] =
  @annotation.tailrec
  def loop(remaining: List[A]): Option[A] =
    remaining match
      case head :: tail =>
        if predicate(head) then Some(head)
        else loop(remaining = tail)

      case Nil =>
        None

  loop(remaining = data)
end findTailRecursive

findTailRecursive(data)(_.isBlank)
findTailRecursive(data)(_.isEmpty)

// Relationship between find & exists.
def existsViaFind[A](data: List[A])(predicate: A => Boolean): Boolean =
  findTailRecursive(data)(predicate).isDefined

// Filter:
// Returns all the element that satisfies a predicate.
data.filter(_.forall(_.isLetter))

// Recursive implementation.
def filterRecursive[A](data: List[A])(predicate: A => Boolean): List[A] =
  data match
    case Nil =>
      Nil

    case head :: tail =>
      val filteredTail = filterRecursive(tail)(predicate)

      if predicate(head) then head :: filteredTail
      else filteredTail
end filterRecursive

filterRecursive(data)(_.forall(_.isLetter))

// Tail-recursive implementation.
def filterTailRecursive[A](data: List[A])(predicate: A => Boolean): List[A] =
  @annotation.tailrec
  def loop(remaining: List[A], acc: List[A]): List[A] =
    remaining match
      case head :: tail =>
        loop(
          remaining = tail,
          if predicate(head) then head :: acc else acc
        )

      case Nil =>
        acc.reverse

  loop(remaining = data, acc = List.empty)
end filterTailRecursive

filterTailRecursive(data)(_.forall(_.isLetter))

println("-----")

println("map")

// Use case:
// Apply a 0.5 bonus to all grades.
val grades = List(0.0d, 2.5d, 3.0d, 4.5d, 5.0d)

def bonus(grades: List[Double]): List[Double] =
  grades.map(grade => math.min(5.0, grade + 0.5))

bonus(grades)

// Tail-recursive implementation.
def mapTailRecursive[A, B](data: List[A])(f: A => B): List[B] =
  @annotation.tailrec
  def loop(remaining: List[A], acc: List[B]): List[B] =
    remaining match
      case head :: tail =>
        loop(
          remaining = tail,
          f(head) :: acc
        )

      case Nil =>
        acc.reverse

  loop(remaining = data, acc = List.empty)
end mapTailRecursive

def bonusTailRecursive(grades: List[Double]): List[Double] =
  mapTailRecursive(grades)(grade => math.min(5.0, grade + 0.5))

bonusTailRecursive(grades)

println("-----")

println("collect & collectFirst")

// Use case:
// Parse a file and discard badly formatted records.
val lines = List(
  "12345",
  "",
  "333.33",
  "    ",
  "0.0",
  "lorem ipsum",
  "10.5"
)

def parse(lines: List[String]): List[Long] =
  lines.map(_.toDoubleOption).collect { case Some(double) =>
    math.round(double)
  }

parse(lines)

// Tail-recursive implementation.
def collectTailRecursive[A, B](
    data: List[A]
)(
    pf: PartialFunction[A, B]
): List[B] =
  val f = pf.lift
  @annotation.tailrec
  def loop(remaining: List[A], acc: List[B]): List[B] =
    remaining match
      case head :: tail =>
        loop(
          remaining = tail,
          f(head).fold(ifEmpty = acc)(b => b :: acc)
        )

      case Nil =>
        acc.reverse

  loop(remaining = data, acc = List.empty)
end collectTailRecursive

def parseTailRecursive(lines: List[String]): List[Long] =
  collectTailRecursive(mapTailRecursive(lines)(_.toDoubleOption)) {
    case Some(double) =>
      math.round(double)
  }

parseTailRecursive(lines)

// Use case:
// Find the radius of the first Circle in a list of Shapes.
enum Shape:
  case Circle(radius: Double)
  case Rectangle(length: Double, width: Double)
  case Triangle(base: Double, height: Double)

val shapesWithoutCircle = List(
  Shape.Rectangle(length = 1, width = 2),
  Shape.Triangle(base = 10, height = 0.1)
)
val shapesWithCircle =
  Shape.Circle(radius = 3.5d) :: shapesWithoutCircle

def radiusOfFirstCircle(shapes: List[Shape]): Option[Double] =
  shapes.collectFirst { case Shape.Circle(radius) =>
    radius
  }

radiusOfFirstCircle(shapes = shapesWithoutCircle)
radiusOfFirstCircle(shapes = shapesWithCircle)

// Tail-recursive implementation.
def collectFirstTailRecursive[A, B](
    data: List[A]
)(
    pf: PartialFunction[A, B]
): Option[B] =
  val f = pf.lift
  def loop(remaining: List[A]): Option[B] =
    remaining match
      case head :: tail =>
        // Technically not tail-recursive,
        // but orElse ensures no stack is preserved.
        f(head) orElse loop(remaining = tail)

      case Nil =>
        None

  loop(remaining = data)
end collectFirstTailRecursive

def radiusOfFirstCircleTailRecursive(shapes: List[Shape]): Option[Double] =
  collectFirstTailRecursive(shapes) { case Shape.Circle(radius) =>
    radius
  }

radiusOfFirstCircleTailRecursive(shapes = shapesWithoutCircle)
radiusOfFirstCircleTailRecursive(shapes = shapesWithCircle)

println("-----")

println("flatMap")

// Use case:
// Get all words of a file.
val text =
  """All code is read more times than it is written.
    |functionality is written once,
    |but probably will be changed many times in the future.
    |Even if it won't be changed,
    |it will have to be understood by someone else in the future.
    |""".stripMargin

def words(text: String): List[String] =
  val lines = text.split('\n').toList
  lines.flatMap { line =>
    line.split(raw"\W+")
  }

words(text)

// Tail-recursive implementation.
def flatMapTailRecursive[A, B](data: List[A])(f: A => List[B]): List[B] =
  @annotation.tailrec
  def loop(remaining: List[A], acc: List[B]): List[B] =
    remaining match
      case head :: tail =>
        loop(
          remaining = tail,
          f(head) reverse_::: acc
        )

      case Nil =>
        acc.reverse

  loop(remaining = data, acc = List.empty)
end flatMapTailRecursive

def wordsTailRecursive(text: String): List[String] =
  val lines = text.split('\n').toList
  flatMapTailRecursive(lines) { line =>
    line.split(raw"\W+").toList
  }

wordsTailRecursive(text)

println("-----")

println("foldLeft & foldRight")

// Use case:
// Combining all the elements of a List together.
val elems = List(1, 2, 3, 4, 5)

def combineAll[A, B](elems: List[A], z: B)(op: (B, A) => B): B =
  elems.foldLeft(z)(op)

combineAll(elems, z = 0)(_ + _)
combineAll(elems, z = "")(_ + _)
combineAll(elems, z = List.empty[Int]) { (acc, age) =>
  age :: acc
}
ages.foldRight(List.empty[Int])(_ :: _)

// Recursive implementation.
def foldRightRecursive[A, B](elems: List[A], z: B)(op: (A, B) => B): B =
  elems match
    case Nil =>
      z

    case head :: tail =>
      op(head, foldRightRecursive(tail, z)(op))
end foldRightRecursive

foldRightRecursive(elems, z = 0)(_ + _)
foldRightRecursive(elems, z = "")(_.toString + _)
foldRightRecursive(elems, z = List.empty[Int])(_ :: _)

// Tail-recursive implementation.
def foldLeftTailRecursive[A, B](elems: List[A], z: B)(op: (B, A) => B): B =
  @annotation.tailrec
  def loop(remaining: List[A], acc: B): B =
    remaining match
      case head :: tail =>
        loop(
          remaining = tail,
          op(acc, head)
        )

      case Nil =>
        acc

  loop(remaining = elems, acc = z)
end foldLeftTailRecursive

foldLeftTailRecursive(elems, z = 0)(_ + _)
foldLeftTailRecursive(elems, z = "")(_ + _)
foldLeftTailRecursive(elems, z = List.empty[Int]) { (acc, age) =>
  age :: acc
}

// Equivalence between foldLeft and foldRight.
def foldRightViaFoldLeft[A, B](elems: List[A], z: B)(f: (A, B) => B): B =
  val accumulateAll = foldLeftTailRecursive(elems, z = identity[B]) {
    (g, a) => b =>
      g(f(a, b))
  }
  accumulateAll(z)

foldRightViaFoldLeft(elems, z = List.empty[Int])(_ :: _)

def foldLeftViaFoldRight[A, B](elems: List[A], z: B)(f: (B, A) => B): B =
  val accumulateAll = foldRightRecursive(elems, z = identity[B]) {
    (a, g) => b =>
      g(f(b, a))
  }
  accumulateAll(z)

foldLeftViaFoldRight(elems, z = List.empty[Int]) { (acc, age) =>
  age :: acc
}

println("-----")

println("groupMapReduce")

// Use case:
// Word count.
def wordCount(text: String): Map[String, Int] =
  words(text).groupMapReduce(
    key = word => word.toLowerCase
  )(_ => 1)(_ + _)

wordCount(text)

// Implementation using foldLeft.
def wordCountFoldLeft(text: String): Map[String, Int] =
  words(text).foldLeft(Map.empty) { case (acc, word) =>
    acc.updatedWith(key = word.toLowerCase) {
      case Some(count) => Some(count + 1)
      case None        => Some(1)
    }
  }

wordCountFoldLeft(text)

println("-----")
