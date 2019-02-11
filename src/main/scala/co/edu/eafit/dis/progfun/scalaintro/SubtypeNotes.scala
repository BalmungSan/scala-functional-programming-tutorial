package co.edu.eafit.dis.progfun.scalaintro

object SubtypeNotes extends App {
  // Subtyping!
  // Subtyping is a very important concept in programming languages.
  // It is so, that there is one SOLID principle dedicated
  // just to provide the correct definition of subtyping.
  // L in SOLID stands for: Liskov substitution principle:
  // "objects in a program should be replaceable with instances
  // of their subtypes without altering the correctness of that program".
  // In Scala for simple types, subtyping is the same as sub-classing,
  // that means that if A extends B then A is a subtype of B (A <: B).
  // Additionally, it is important to note that for every type in Scala:
  //   * It is a subtype, and also a sub-class, of Any.
  //   * It is a supertype, but not a super-class, of Nothing.
  // Also for any reference type, namely a type that is not a primitive
  // and is defined by a class, trait or object, we can say that:
  //   * It is a subtype, and also a sub-class, of AnyRef.
  //     This is the equivalent of java.lang.Object, AnyRef <: Any.
  //   * It is a supertype, but not a super-class, of Null.
  //     There is only one instance of Null and it is null,
  //     it is the same null as from java, Nothing <: Null.
  println("- Subtyping -")
  sealed trait Pet {
    def name: String
  }
  final case class Dog(override val name: String) extends Pet
  final case class Cat(override val name: String) extends Pet
  println(
    """sealed trait Pet {
      |  def name: String
      |}
      |final case class Dog(override val name: String) extends Pet
      |final case class Cat(override val name: String) extends Pet""".stripMargin
  )
  def printPet(pet: Pet): String = pet.toString
  println("def printPet(pet: Pet): String = pet.toString")
  val cat: Cat = Cat(name = "Luzy")
  val printedCat = printPet(pet = cat)
  println(s"Given cat = Cat(name = 'Luzy')\t->\tprintPet(pet = cat) = ${printedCat}")
  // However, for higher-kinded types the subtyping relationship becomes complicated.
  println()

  // Variance!
  // Variance determines how will be the subtyping relationship of
  // higher-kinded types, given the relationship of their component types.
  // Given two types A & B, such that A <: B, and a higher-kinded type C[_],
  // then there are three possibilities:
  //   * Covariance: C[A] <: C[B], it is denoted with a plus sign (+) when defining C[+T].
  //   * Contravariance: C[B] <: C[A], it is denoted with a minus sign (-) when defining C[-T].
  //   * Invariance: No relationship between C[A] and C[B], it is the default behavior when defining C[T].
  println("- Variance -")

  // Covariance!
  println("-- Covariance --")
  // We will define our own List to show how covariance works.
  // Since Lists are covariant, then if a method expects a List[B],
  // a List[A] can always be used instead.
  // This makes sense because we can only use, but not modify, what is inside the List.
  // And since any A can be used where a B is expected, then it makes sense that
  // List[A] <: List[B].
  sealed trait List[+A] {
    def ::[B >: A](elem: B): List[B] =
      Cons(head = elem, tail = this)
    def map[R](f: A => R): List[R] = this match {
      case Nil         => Nil
      case Cons(x, xs) => Cons(f(x), xs.map(f))
    }
    override final def toString: String = {
      def loop(acc: String, remaining: List[A]): String = remaining match {
        case Nil         => acc
        case Cons(x, xs) => loop(s"${acc}, ${x}", remaining = xs)
      }
      this match {
        case Nil         => "List()"
        case Cons(x, xs) => s"List(${loop(x.toString, remaining = xs)})"
      }
    }
  }
  final case class Cons[+T](head: T, tail: List[T]) extends List[T]
  case object Nil extends List[Nothing]
  object List {
    def apply[T](elements: T*): List[T] = {
      if (elements.nonEmpty) Cons(head = elements.head, tail = List(elements.tail: _*)) else Nil
    }
  }
  println(
    """sealed trait List[+A]
      |final case class Cons[+A](head: A, tail: List[A]) extends List[A]
      |case object Nil extends List[Nothing]""".stripMargin
  )
  def petsNames(pets: List[Pet]): List[String] =
    pets.map(pet => pet.name)
  println("def petsNames(pets: List[Pet]): List[String] = pets.map(pet => pet.name)")
  val dogs: List[Dog] = List(Dog("Lucas"), Dog("Charlie"))
  val dogsNames = petsNames(pets = dogs)
  println(s"Given dogs = List(Dog('Lucas'), Dog('Charlie'))\t->\tpetsNames(pets = dogs) = ${dogsNames}")

  // Contravariance!
  // For showing contravariance we will be using functions.
  // In Scala functions contravariant in their parameters and covariant in the result.
  // This means that if a method expects a function that accepts elements of type A,
  // a function that accepts elements of type B can always be used instead.
  // This makes sense since what a function does is to consume elements,
  // and under the assumption that they are total,
  // if a function is capable of handling any element of type B,
  // then it must be able to handle any element of type A.
  println("-- Contravariance --")
  println(
    """sealed trait List[+A] {
      |  def map[R](f: A => R): List[R] = this match {
      |    case Nil         => Nil
      |    case Cons(x, xs) => Cons(f(x), xs.map(f))
      |  }
      |}""".stripMargin
  )
  val petToName: Pet => String = pet => pet.name
  println(s"Given petToName = (pet: Pet) => pet.name\t->\tdogs.map(petToName) = ${dogs.map(petToName)}")

  // Invariance!
  // Any kind of mutable structure has to be invariant,
  // that means that it is neither covariant nor contravariant.
  // This makes sense, because a mutable structure works both like
  // a producer and a consumer of elements.
  println("-- Invariance --")
  final class MutableBox[A](initialValue: A) {
    private[this] var value: A = initialValue
    def getValue: A = value
    def setValue(newValue: A): Unit = this.value = newValue
    override def toString: String = s"Box { value = ${value} }"
  }
  println(
    """final class MutableBox[A](initialValue: A) {
      |  private[this] var value: A = initialValue
      |  def getValue: A = value
      |  def setValue(newValue: A): Unit = this.value = newValue
      |  def toString: String = s"Box { value = ${value} }"
      |}""".stripMargin
  )
  val intBox = new MutableBox(initialValue = 10)
  println(s"Given intBox = new MutableBox(initialValue = 10)\t->\t${intBox}")
  intBox.setValue(newValue = 5)
  println(s"intBox.setValue(newValue = 5) = ${intBox}")
  // At first sight, it looks like this MutableBox could be covariant.
  // The reason why a mutable data structure may no be covariant is because mutability.
  // That could lead to runtime type errors, to show how consider the following code:
  // val dogBox: MutableBox[Dog] = new MutableBox(initialValue = Dog(name = "Lucas"))
  // val petBox: MutableBox[Pet] = dogBox
  // petBox.setValue(newValue = Cat(name = "Luzy"))
  // val dog: Dog = dogBox.getValue
  // The above line is correct given the type system, but does not makes sense
  // since now, the value of dogBox is not longer a Dog but a Cat.
  // Hopefully, scala would not let this piece of code compile
  // Because MutableBox can not be made covariant in a first place.
  // Note: In Java, every array is covariant and every generic is invariant,
  //       and there is no way to change or control this behavior.
  //       Given that, the above code could be written in Java using Arrays
  //       instead of MutableBox, the code will compile and fail in runtime.
  println()

  // Type bounds!
  // Type bound can be used when defining a generic class or a generic method
  // to limit the possible values to be used in the generic.
  println("- Type bounds -")

  // Upper type bounds!
  // As the name suggest, these are used to limit the generic type parameter
  // to be a sub-type of other type.
  // For example, lets create a immutable PetContainer class
  // which can store values of any type P as long as these are a subtype of Pet.
  println("-- Upper type bounds --")
  final case class PetContainer[P <: Pet](pet: P) {
    val petName: String = pet.name // We can use any method defined in the Pet trait.
  }
  println(
    """final case class PetContainer[P <: Pet](pet: P) {
      |  val petName: String = pet.name
      |}""".stripMargin
  )
  val dogContainer = PetContainer(pet = Dog("Lucas"))
  val dog: Dog = dogContainer.pet
  println(s"PetContainer(pet = Dog('Lucas')).pet = ${dog}")
  // At first glance it might seems like the use of the P generic
  // and the type bound is unnecessary, since we can just say that
  // the pet argument to be of type Pet.
  // However, there is a big advantage of using the the P generic
  // and it is that the pet value does not forget what is its type.
  // Thus, when accessing it again we can safely store it in a variable
  // of type Dog, which could not be done if it were upcasted to Pet.

  // Lower type bounds!
  // As the name suggest, these are used to limit the generic parameter
  // to be a supertype of other type.
  // At first it seems that it is not useful at all,
  // however it does has its utility when used together with other type parameters.
  // For example, lets add a :: method to our List, that allows us to prepend
  // and element to the List.
  // At first we may think we could just write `def ::(elem: A): List[A]`
  // "However, this program does not compile, because the parameter elem in :: is of type A,
  // which we declared covariant. This doesn’t work because functions are
  // contravariant in their parameter types and covariant in their result types.
  // To fix this, we need to flip the variance of the type of the parameter element in add,
  // we do this by introducing a new type parameter B that has A as a lower type bound".
  //  - Reference: https://docs.scala-lang.org/tour/lower-type-bounds.html
  // In a more intuitive way, if you add something to a List, then:
  //   * It must be an A - in this case the List is still a List[A].
  //   * Or it must be any subtype of A - in this case
  //     the element gets upcasted to A, and the List remains a List[A].
  //   * Or if it is another type B, which must be a supertype of A - in this case
  //     the List gets upcasted to a List[B].
  //     Note: Because Any is just a supertype of everything,
  //           in the worst case the List will be upcasted to List[Any].
  println("-- Lower type bounds--")
  println(
    """sealed trait List[+A] {
      |  def ::[B >: A](elem: B): List[B] =
      |    Cons(head = elem, tail = this)
      |}""".stripMargin
  )
  val catList: List[Cat] = List(Cat("cat1"), Cat("cat2"))
  val petList: List[Pet] = Dog("dog1") :: catList
  println(s"given catList = List(Cat('cat1'), Cat('cat2'))\t->\tDog('dog1') :: catList = ${petList}")
  println()

  // Generalized type constraints!
  // Sometimes, we want to provide complex type restrictions to our functions.
  // Or we want to extract very specific type information about our arguments,
  // to provide a most concrete return type.
  // In those cases, type bounds may not work well and we need a more powerful
  // constructor - in those cases one can use the generalized type constraints.
  // There are two constraints:
  //   * Subtype constraint, A <:< B, proves that A is a subtype of B.
  //   * Sametype constraint, A =:= B, proves that A is the same type as B.
  // They work as an implicit evidence that the compiler will try to fill.
  // Note: For more information read the "Generalized type constraints in Scala" Blog.
  println("- Generalized type constraint -")
  object TypeChecker {
    def isSubType[A, B](a: A, b: B)(implicit ev: A <:< B = null): Boolean =
      Option(ev).fold(ifEmpty = false)(_ => true)
    def isSameType[A, B](a: A, b: B)(implicit ev: A =:= B = null): Boolean =
      Option(ev).fold(ifEmpty = false)(_ => true)
  }
  println(
    """object TypeChecker {
      |  def isSubType[A, B](a: A, b: B)(implicit ev: A <:< B = null): Boolean =
      |    Option(ev).fold(ifEmpty = false)(_ => true)
      |  def isSameType[A, B](a: A, b: B)(implicit ev: A =:= B = null): Boolean =
      |    Option(ev).fold(ifEmpty = false)(_ => true)
      |}""".stripMargin
  )
  println(s"Int subtype of String is ${TypeChecker.isSubType(0, "")}")
  println(s"Dog subtype of Pet is ${TypeChecker.isSubType(Dog(""), new Pet { val name = "" })}")
  println(s"Dog sametype of Pet is ${TypeChecker.isSameType(Dog(""), new Pet { val name = "" })}")
  println(s"Int sametype of Int is ${TypeChecker.isSameType(3, 5)}")
}
