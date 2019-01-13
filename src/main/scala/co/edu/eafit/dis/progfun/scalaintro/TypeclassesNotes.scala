package co.edu.eafit.dis.progfun.scalaintro

object TypeclassesNotes extends App {
  // Typeclasses!
  // The typeclass pattern, originally created in Haskell,
  // is a powerful mechanism to achieve ad-hoc polymorphism.
  // The main idea behind them is simple:
  // "Segregate a type definition, from the implementation of its behaviors".
  // To show how to implement it and show how it differs from
  // normal (subtyping) polymorphism, we will implement a simple Printable
  // interface, that provides an asString method that turns a value
  // of the implemented type to a String representation.
  // Note: We all know that Scala runs over the JVM.
  //       Thus, every Scala object is a Java object.
  //       And as such, every Scala object has a toString method,
  //       that does exactly the same.
  //       Lets just ignore that, for the sake of the example.

  // Subtyping polymorphism!
  // This is the most common form of polymorphism found in OOP languages (like Java).
  // The idea is simple, one first defines an interface that defines the functions
  // that are available on types implementing the interface.
  // Then, when one is defining the new type, one must implements the interface.
  println("- Subtyping polymorphism -")
  trait Printable {
    def asString: String
  }
  final case class User1(name: String, age: Int) extends Printable {
    override def asString: String = s"User { name: '${name}', age: '${age}' }"
  }
  println(
    """trait Printable {
      |  def asString: String
      |}
      |final case class User(name: String, age: Int) extends Printable {
      |  override def asString: String = s"User { name: '${name}', age: '${age}' }"
      |}""".stripMargin
  )
  val user1 = User1(name = "Luis Miguel Mejía Suárez", age = 21)
  println(s"Given user = User(name = 'Luis Miguel Mejía Suárez', age = 21)\t->\tuser.asString = ${user1.asString}")
  // Great... However what if:
  // We want a Printable instance of a class defined in an external library,
  // or if we want any standard scala class to be Printable?
  // We can not, because those types are already defined and one can not make them
  // extend the new interface, even if one know how to do the implementation.
  // A common workaround is to create a wrapper class that implements the interface
  // for the wrapped type, but that introduces code overhead and hide our intentions.
  // Another problem that this technique has, is that if we want our type to implement
  // many different behaviors, then the type definition grows in code length
  // and complexity, making it harder to maintain.
  println()

  // Typeclasses are other technique for achieving polymorphism, they were first
  // introduced in Haskell in the late 90's.
  // Scala does not provide native support to typeclasses perse, but by abusing
  // the implicits mechanism of the language we can implement them.
  // The idea of the typeclass pattern is to segregate the type definition
  // from the implementation of an interface for it.
  println("- Typeclass (ad-hoc polymorphism) -")

  // The first step is to define the interface and the new type,
  // and implement the interface for the new type.
  println("-- Interface definition & implementation --")
  trait Printer[T] {
    // Note that this time the method takes an instance of the type.
    // Also, that the interface is generic on a type T.
    def print(t: T): String
  }
  final case class User2(name: String, age: Int)
  implicit val UserPrinter: Printer[User2] = new Printer[User2] {
    override def print(user: User2): String =
      s"User { name: '${user.name}', age: '${user.age}' }"
  }
  println(
    """trait Printer[T] {
      |  def print(t: T): String
      |}
      |final case class User(name: String, age: Int)
      |implicit val UserPrinter: Printer[User2] = new Printer[User2] {
      |  override def print(user: User2): String =
      |    s"User { name: '${user.name}', age: '${user.age}' }"
      |}""".stripMargin
  )
  val user2 = User2(name = "BalmungSan", age = 35)
  val user2Printed = implicitly[Printer[User2]].print(user2)
  println(s"Given user = User(name = 'BalmungSan', age = 35)\t->\timplicitly[Printer[User]].print(user) = ${user2Printed}")

  // The above example worked great, but invoking the function on the instance
  // was quite tricky and feels unnatural.
  // Is there a simple way to get back the dot notation syntax we are used to?
  // The answer is yes, we can combine the typeclass pattern with the Ops pattern.
  println("-- Ops pattern --")
  object syntax {
    object printer {
      implicit class PrinterOps[T](val t: T) extends AnyVal {
        // Provides the asString method to any instance of any type T,
        // as long as there is a Printer for such type T.
        def asString(implicit TPrinter: Printer[T]): String = TPrinter.print(t)
      }
    }
  }
  println(
    """object syntax {
      |  object printer {
      |    implicit class PrinterOps[T](val t: T) extends AnyVal {
      |      def asString(implicit TPrinter: Printer[T]): String = TPrinter.print(t)
      |    }
      |  }
      |}""".stripMargin
  )
  import syntax.printer._ // Import the PrintOps implicit class in the scope.
  println(s"Given user = User(name = 'BalmungSan', age = 35)\t->\tuser.asString = ${user2.asString}")

  // Given the implementation of the typeclass for a type
  // is done separately from the type definition.
  // One can provide implementations of it for any type that is already defined.
  // It is common that many users will need instances for common types
  // from the standard library.
  // We can save them the work of implementing them
  // by putting the instances in the companion object of the typeclass.
  // This way, these will always be in the implicit scope.
  println("-- Instance derivation --")
  object Printer {
    implicit val StringPrinter: Printer[String] = new Printer[String] {
      override def print(s: String): String = s
    }
    implicit val IntPrinter: Printer[Int] = new Printer[Int] {
      override def print(i: Int): String = i.toString
    }
    // This is called implicit derivation.
    // We can create a Printer instance of any List[T] for any type T,
    // as long as there is a Printer instance for this type.
    // These instances will be created on the fly when needed.
    implicit def listPrinter[T](implicit TPrinter: Printer[T]): Printer[List[T]] =
      new Printer[List[T]] {
        override def print(l: List[T]): String =
          l.map(t => TPrinter.print(t)).mkString("[", ", ", "]")
      }
  }
  println(
    """object Printer {
      |  implicit def listPrinter[T](implicit TPrinter: Printer[T]): Printer[List[T]] =
      |    new Printer[List[T]] {
      |      override def print(l: List[T]): String =
      |        l.map(t => TPrinter.print(t)).mkString("[", ", ", "]")
      |    }
      |}""".stripMargin
  )
  val userList =
    List(
      User2(name = "Luis Miguel Mejía Suárez", age = 21),
      User2(name = "BalmungSan", age = 35)
    )
  println(s"Given userList = List(User(name = 'Luis Miguel Mejía Suárez', age = 21), User(name = 'BalmungSan', age = 35))\t->\tuserList.asString = ${userList.asString}")
  // After seeing how we derived a Printer instance for List[T],
  // we may think that it should be possible to do the same for tuples
  // and case classes (which are like named tuples).
  // The answer is yes, it is possible, since a tuple can be seen
  // as a heterogeneous list (a list which can have elements of different types),
  // and as the same way we derived the instance for a List[T] we can do the
  // same for an HList[T, U ..., Z] as long as we have the Printer instance for
  // all types that are inside the HList.
  // The problem is that doing that is not a trivial work, we will see that
  // when we take a look to shapeless that an implementation of HList,
  // together with other abstractions that are useful for
  // implementing generic derivations of typeclasses.

  // Note: This was just a simple introduction to the typeclass pattern.
  //       For a more extended discussion about polymorphism strategies in Scala,
  //       and a comparison between them. Read this article:
  //       https://gist.github.com/BalmungSan/c19557030181c0dc36533f3de7d7abf4
}
