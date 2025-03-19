println("Classes -----")

// In Scala, you declare the constructor with the class name, and the body is its constructor.
class SimpleClass(a: Int, val b: String):
  println(s"Constructing a simple class with ${a} and ${b}")

// The new keyword is optional.
val aSimpleClass =
  /** new */
  SimpleClass(a = 10, b = "Foo")

// By default, the string representation of a class is not very useful.
println(aSimpleClass)

// You can't access the properties of the constructor, unless explicitly stated with val / var:
// aSimpleClass.a // Doesn't compile.
aSimpleClass.b

// And equality is done by-reference rather than by value.
val anotherSimpleClass = SimpleClass(a = 10, b = "Foo")
aSimpleClass == anotherSimpleClass

println("-----")

println("Case classes -----")

// When working with immutable data properties like:
//  - Human readable string representation
//  - Transparency of the data
//  - By-value equality
//  - Easy copying
//  - Pattern matching
// Are very useful, Scala provides a concept called case classes,
// which is nothing more than sugar syntax over normal classes,
// reducing the amount of boilerplate one needs to write.

case class SimpleCaseClass(a: Int, b: String)
val aSimpleCaseClass = SimpleCaseClass(a = 10, b = "bar")

// Human readable string representation
println(aSimpleCaseClass)

// All constructor properties are visible by default.
aSimpleCaseClass.a
aSimpleCaseClass.b

// Equality is by-value.
val anotherSimpleCaseClass = SimpleCaseClass(a = 10, b = "bar")
aSimpleCaseClass == anotherSimpleCaseClass

// If by-reference equality is needed, you can use the eq method.
aSimpleCaseClass eq anotherSimpleCaseClass
aSimpleCaseClass eq aSimpleCaseClass

// You can create a copy of a case class by altering only the fields you want.
aSimpleCaseClass.copy(a = 35)

// You can pattern match on a case class to extract its data.
aSimpleCaseClass match {
  case SimpleCaseClass(a, b) =>
    println(s"Case class data: a = ${a} | b = ${b}")
}

println("-----")

println("Interfaces and methods -----")

// To create an interface you use the 'trait' keyword.
// These can have as many abstract methods as you want.
// Methods are defs inside a class / trait, these can take parameters.

trait Shape:
  def area: Double

// A class can then extend multiple traits and implement all the abstract methods.
case class Circle(r: Double) extends Shape:
  override def area: Double =
    math.Pi * math.pow(this.r, 2)

  def *(n: Double): Circle =
    this.copy(r = this.r * n)
// Note: All the 'this' are redundant here, I added them as a matter of style preference.

// Calling methods.
val smallCircle = Circle(r = 1)
println(smallCircle)
// Parameterless methods look like properties.
smallCircle.area
val bigCircle = smallCircle * 5
println(bigCircle)
bigCircle.area

println("-----")

println("Functions -----")

// Functions and methods are different things; although very similar.
// Functions are values, and as such can be passed as arguments to other functions / methods, as well as being returned.
// When you call a function, you are just calling a special method inside of it called 'apply'.
// Methods are not values, you can't pass a method to another.
// However, while understanding this difference is very important to understand the nature of Scala.
// Most folks would mix the words informally.

// Lambda syntax is a simple way to declare functions.
// The language has type inference, so as long as you help it a little it is able to infer the types.
val f1: Int => Int =
  x =>
    x + 1 // The language is able to infer the type of 'x' from the type of 'f'.
val f2 =
  (x: Int) =>
    x + 1 // Alternatively, you can specify the type of 'x' and let the language infer the type of 'f'.
// Lambda syntax is sugar syntax for an object declaration.
val f3 = new Function1[Int, Int]:
  override def apply(x: Int): Int =
    x + 1

// Calling a function is just sugar syntax for calling the 'apply' method.
f1(10)
f2.apply(10)
f3.apply(10)
// Note: That sugar syntax is universal to all types; not only functions.

// Top-level defs are technically methods on an invisible object.
// But is common to just call them functions for simplicity.
def f4(x: Int): Int = x + 1
// In this case there is no apply sugar syntax, we are just calling the method directly.
f4(10)

// Recursion.
// A function can call itself.

// Using if.
def fact1(n: Int): Int =
  if (n == 0) then 1 else n * fact1(n - 1)

fact1(10)

// Using pattern matching.
def fact2(n: Int): Int =
  n match
    case 0 => 1
    case _ => n * fact2(n - 1)

fact2(10)

// Tail-recursion, stack safe.
def fact3(n: BigInt): BigInt =
  // Essentially an immutable while loop.
  @annotation.tailrec
  def loop(curr: BigInt, acc: BigInt): BigInt =
    if (curr == 0) then
      // Base case returns the accumulator.
      acc
    else
      // Recursive case does the recursion as the last action (tail)
      // And uses the accumulator to track the result.
      loop(curr = curr - 1, acc = acc * curr)

  // The loop starts with the initial value,
  // and the accumulator with the base case.
  loop(curr = n, acc = 1)

fact3(1000)

// A tail-recursive function can always be transformed into a while loop, and vice-versa.
def fact4(n: BigInt): BigInt =
  var acc: BigInt = 1
  var curr = n
  while (curr > 0) {
    acc *= curr // acc = acc * curr
    curr -= 1 // n2 = n2 - 1
  }
  acc

fact4(1000)

// Type parameters / Generics.
// Methods can have type parameters (functions can't in Scala 2, in Scala 3 there is a special kind of polymorphic functions).
// These are very useful to write reusable logic that preserves type information.
// Let's imagine a simple debug function that takes a value, print it and returns it unchanged.
def debug1(data: String): String =
  println(s"Debug: ${data}")
  data
val data1 = debug1("data")
// This is useful but we would need to repeat it for all types.
// We may be tempted to use Any:
def debug2(data: Any) =
  println(s"Debug: ${data}")
  data
val data2 = debug2("data")
// But now data2 lost its type and we can't call String methods like 'size' on it.
// The solution is to use a type parameter to make the method generic
// and let the compiler properly infer and preserve the type information.
def debug3[A](data: A): A =
  println(s"Debug: ${data}")
  data
val data3 = debug3("data")
data3.size

// A method / function can receive and return other functions but not other methods.
// This is called higher-order functions.
def applyTwice[A](f: A => A): A => A =
  x => f(f(x))

val g1 = applyTwice(f1)
g1(10)

// Rather than using lambda syntax,
// we could have used some helper methods to combine functions.
val g2 = f2 andThen f2 // This goes ((A => B) andThen (B => C)) => (A => C)
g2(10)
val g3 = f3 compose f3 // This goes ((B => C) andThen (A => B)) => (A => C)
g3(10)

// Technically speaking, we should not be able to pass a def to 'applyTwice',
// However this works:
val g4 = applyTwice(f4)
g4(10)
// This is thanks to a feature of the language called eta-expansion.
// You can imagine the compiler created a lambda like:
val g5 = applyTwice(x => f4(x))
g5(10)

println("-----")

println("ADTs -----")

// Algebraic Data Types
// Is the way we model domains in FP languages using immutable records.
// We have two basic constructs: Products and Sums (and thus the name algebraic)
// Products mean grouping multiple types together in a single record.
// Sums mean allowing for a type to be one or many options.
// Case classes are products and enums are sums

// A User is either a Client or an Administrator.
// All kinds of users have an id and a name.
// A Client has an id, a name, and a balance.
// An Administrator has an id, a name, and a list of permissions.
enum User(id: Int, name: String):
  case Client(id: Int, name: String, balance: Double) extends User(id, name)
  case Administrator(id: Int, name: String, permissions: List[String])
      extends User(id, name)

  def debug(): Unit =
    println(s"User ${id} - ${name}")

val user1: User = User.Client(id = 1, name = "Luis", balance = 135)
val user2: User = User.Administrator(
  id = 2,
  name = "Miguel",
  permissions = List("foo", "bar", "baz")
)

// We can use common methods of the parent type.
user1.debug()
user2.debug()

// Or use pattern matching to determine the specific case.
def logic(user: User): Unit =
  user match
    case User.Client(_, _, balance) =>
      println(s"Client with balance: $$ ${balance}")

    case User.Administrator(_, _, permissions) =>
      println(s"Administrator with permissions: ${permissions.mkString(", ")}")

logic(user1)
logic(user2)

println("-----")

println("Common ADTs -----")

// The standard library already provides three very common and useful ADTs:
// Option, Either, Try.

// Option.
// Represents the possibility of absence of a value.
// It solves the same problem that null, but in a type safe way.
// Since an Option[A] is not an A, you can not use it in a way that would trigger a NPE.
// Rather, you are forced to deal with the possibility of absence:
// * Via pattern matching, to inspect inside the ADT.
// * Using eliminators, to remove the effect; like: getOrElse, or fold.
// * Using combinators, to compose multiple options together; like: map, flatMap, filter, etc.
enum MyOption[+A]:
  case MySome(a: A)
  case MyNone

def safeDivision(a: Int, b: Int): Option[Int] =
  if (b == 0) then None else Some(a / b)

safeDivision(10, 5)
safeDivision(0, 1)
safeDivision(3, 0)

Option(10).getOrElse(default = 0)
Option(null).getOrElse(default = 0)
Option(10).fold(ifEmpty = false)(v => v > 5)
Option.empty[Int].fold(ifEmpty = false)(v => v > 5)

// Either:
// Represents a basic union type.
// When you have an Either[A, B] then you either have an A or a B (as the name implies).
// Meaning that you need to account for any of the two possibilities.
// Usually the Left is used for errors and the Right for successful values, but it is more general.
// Similar than option, you can use pattern matching, eliminators, and combinators.
enum MyEither[+A, +B]:
  case MyLeft(a: A)
  case MyRight(b: B)

final case class Error(msg: String)

def parseAndValidateAge(rawAge: String): Either[Error, Int] =
  rawAge.toIntOption
    .toRight(left = Error("Age is not a valid integer"))
    .flatMap { age =>
      if age >= 18 then Right(age) else Left(Error("User is under age"))
    }

parseAndValidateAge("21")
parseAndValidateAge("13")
parseAndValidateAge("ten")

// Try:
// Similar to Either, but focuses only exceptions.
// Its cases are called Failure and Success to represent that.
enum MyTry[+A]:
  case MyFailure(ex: Throwable)
  case MySuccess(a: A)

util.Try(10 / 2)
util.Try(10 / 0)

println("-----")
