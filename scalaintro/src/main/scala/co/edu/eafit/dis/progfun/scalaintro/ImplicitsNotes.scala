package co.edu.eafit.dis.progfun.scalaintro

import scala.language.implicitConversions // Needed to enable implicit conversions.

object ImplicitsNotes extends App {
  // Implicits!
  // One of the most powerful (and thus dangerous) feature of Scala,
  // is its ability to do things implicitly.
  // In particular there are 4 kinds of implicits to which we will look:
  //   1. Implicit values.
  //   2. Implicit parameters.
  //   3. Implicit conversions.
  //   4. Implicit classes (extension methods).
  println("- Implicits -")

  // Implicit values!
  // Implicit values are like regular values,
  // except that they become part of the implicit scope,
  // as such you can ask for them implicitly.
  // Given this they may be any kind of value, including objects.
  // However, it is recommended that they should only be simple vals,
  // with explicit type signature, to avoid unexpected behaviors.
  // It is also mandatory to ensure there is only one instance
  // for each type you want to access implicitly.
  println("-- Implicit values --")
  trait SomeTrait[T] {
    def f: T
  }
  trait ExtendedTrait[T] extends SomeTrait[T] {
    def g: T
  }
  /* implicit */ object SomeStringObject extends SomeTrait[String] {
    override def f: String = "from object f"
  }
  implicit object ExtendedStringObject extends ExtendedTrait[String] {
    override def f: String = "from extended obj f"
    override def g: String = "from extended obj g"
  }
  implicit val SomeStringVal: SomeTrait[String] = new SomeTrait[String] {
    override def f: String = "from val f"
  }
  implicit val ExtendedStringVal: ExtendedTrait[String] = new ExtendedTrait[String] {
    override def f: String = "from extended val f"
    override def g: String = "from extended val g"
  }
  println(
    """trait SomeTrait[T] {
      |  def f: T
      |}
      |trait ExtendedTrait[T] extends SomeTrait[T] {
      |  def g: T
      |}
      |implicit object SomeStringObject extends SomeTrait[String] {
      |  override def f: String = "from object f"
      |}
      |implicit object ExtendedStringObject extends ExtendedTrait[String] {
      |  override def f: String = "from extended obj f"
      |  override def g: String = "from extended obj g"
      |}
      |implicit val SomeStringVal: SomeTrait[String] = new SomeTrait[String] {
      |  override def f: String = "from val f"
      |}
      |implicit val ExtendedStringVal: ExtendedTrait[String] = new ExtendedTrait[String] {
      |  override def f: String = "from extended val f"
      |  override def g: String = "from extended val g"
      |}""".stripMargin
  )
  // If there are several eligible arguments which match the implicit parameter's type,
  // the most specific one will be chosen using the rules of static overloading resolution.
  println(s"implicitly[SomeTrait[String]].f = ${implicitly[SomeTrait[String]].f}")
  // Note that if SomeStringObject would be implicit,
  // the above statement would result in a compilation error:
  // > ambiguous implicit values:
  // > both object SomeStringObject of type SomeStringObject.type
  // > and object ExtendedStringObject of type ExtendedStringObject.type
  // > match expected type SomeTrait[String]
  // The above (somewhat surprising) result and the exception,
  // are mainly caused because objects have their own type.
  // That is an example of why you should not use implicit objects,
  // and why one should only implement the most concrete trait possible,
  // to ensure there is always only one implicit instance in scope.
  println()

  // Implicit parameters!
  // A function may have an implicit parameters list,
  // if, when calling the function, these parameters are not explicitly specified,
  // then the compiler will search for them in the implicit scope.
  // There can only be one implicit parameters list,
  // and it has to be the last one - however, it may have multiple parameters.
  // Note: Read "Where Does Scala Look For Implicits?"
  //       for a detailed explanation of how the mechanism works.
  println("-- Implicit parameters --")
  def implicitSum(a: Int)(implicit b: Int): Int = a + b
  println("def implicitSum(a: Int)(implicit b: Int): Int = a + b")
  implicit val five = 5
  val implicitSumed = implicitSum(10)
  println(s"Given implicit five = 5\t->\timplicitSum(10) = ${implicitSumed}")
  // You can also pass an implicit parameter explicitly.
  val explicitSumed = implicitSum(10)(3)
  println(s"implicitSum(10)(3) = ${explicitSumed}")
  // Note: This was a simple example to show how implicit parameter work.
  //       In real code, one must never use such a trivial type
  //       like Int for an implicit parameter.
  //       Given there can only be one implicit instance for the asked type.
  println()

  // Implicit conversions!
  // An implicit conversion is just a function of one argument,
  // which is marked with the implicit keyword.
  // They will be used by the compiler to cats a value of the input type to the return type,
  // when the value is used in a place where is expected a value of a different type.
  println("-- Implicit conversions --")
  implicit def int2boolean(i: Int): Boolean = i > 0
  println("implicit def int2boolean(i: Int): Boolean = i > 0")
  val casted1: Boolean = 10
  println(s"10: Boolean = ${casted1}")
  val casted2: Boolean = 0
  println(s"0: Boolean = ${casted2}")
  // Note: Given they work pretty much like black magic, its use is not recommended.
  //       And one must warn the compiler of its use by means of a special import
  //       that works as a feature flag.
  println()

  // Implicit classes!
  // Implicit classes are like regular classes,
  // whose constructor is called implicitly by the compiler
  // when you call a method on a type that it does not have.
  // They must take only one non-implicit argument in their constructor,
  // and they must be defined inside another object, class or trait.
  // They are usually used to provide (extension) methods to existing types.
  // Given they are only used for their methods,
  // its type becomes irrelevant as it is never used nor asked for.
  // For that reason they are considered safer and more controllable that implicit conversions.
  // Thus, they are widely used and do not require any feature flag to be activated.
  // Additionally, given they are used only to add extension methods,
  // they are usually used together with value classes,
  // to provide them whit out the need to allocate a new class.
  // Note: At bytecode level an implicit class is just a regular class
  //       together with an implicit conversion from its argument to the class.
  println("-- Implicit classes & Extention methods --")
  implicit class StringOps(val s: String) extends AnyVal {
    def toIntOption: Option[Int] =
      scala.util.Try(s.toInt).toOption
  }
  println(
    """implicit class StringOps(val s: String) extends AnyVal {
      |  def toIntOption: Option[Int] = util.Try(s.toInt).toOption
      |}""".stripMargin
  )
  // Because neither the String class, nor any of it super classes,
  // provides a 'toIntOption' method.
  // The compiler will search for an implicit conversion from String
  // to something which provides such method - in this case 'StringOps'.
  // And because 'StringOps' is a value class,
  // the compiler will optimize the following line as a method call.
  // And probably in runtime the JIT will just inline the method call.
  val parsed1 = "123".toIntOption
  println(s"'123'.toIntOption = ${parsed1}")
  val parsed2 = "Hello, World!".toIntOption
  println(s"'Hello, World!'.toIntOption = ${parsed2}")
}
