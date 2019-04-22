package co.edu.eafit.dis.progfun.scalaintro

object SubtypeNotes extends App {
  // Subtyping!
  // Subtyping is a very important concept in programming languages.
  // It is so, that there is one SOLID principle dedicated
  // just to provide the correct definition of subtyping.
  // The L in SOLID stands for: Liskov substitution principle.
  // "Objects in a program should be replaceable with instances
  // of their subtypes without altering the correctness of that program".
  // In Scala for simple types, subtyping is the same as sub-classing,
  // that means that if T extends U, then T is a subtype of U (T <: U).
  // Additionally, it is important to note that for every type in Scala:
  //   * It is a subtype, and also a sub-class, of Any.
  //   * It is a supertype, but not a super-class, of Nothing.
  // Also for any reference type, namely a type that is not a "primitive"
  // and is defined by a class, trait or object, we can say that:
  //   * It is a subtype, and also a sub-class, of AnyRef.
  //     This is the equivalent of java.lang.Object. AnyRef <: Any.
  //   * It is a supertype, but not a super-class, of Null.
  //     There is only one instance of Null and it is null,
  //     it is the same null as from java. Nothing <: Null.
  println("- Subtyping -")
  sealed trait Pet extends Product with Serializable {
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
  println()

  // Variance!
  // Variance determines how will be the subtyping relationship of
  // higher-kinded types, given the relationship of their component types.
  // Given two types T & U, such that T <: U, and a higher-kinded type C[_],
  // then there are three possibilities:
  //   * Covariance: C[T] <: C[U], it is denoted with a plus sign (+) when defining C[+T].
  //   * Contravariance: C[U] <: C[T], it is denoted with a minus sign (-) when defining C[-T].
  //   * Invariance: No relationship between C[T] and C[U], it is the default behavior when defining C[T].
  println("- Variance -")

  // Covariance!
  println("-- Covariance --")
  // We will define our own List to show how covariance works.
  // Since Lists are covariant, then if a method expects a List[U], a List[T] can always be used instead.
  // This makes sense because we can only use, but not modify, what is inside the List.
  // And since any T can be used where a U is expected, then List[T] <: List[U] is concluded.
  sealed trait List[+T] extends Product with Serializable {
    def ::[U >: T](elem: U): List[U] =
      Cons(head = elem, tail = this)
    def map[R](f: T => R): List[R] = this match {
      case Nil         => Nil
      case Cons(x, xs) => Cons(f(x), xs.map(f))
    }
    override final def toString: String = this match {
      case Nil         => "Nil"
      case Cons(x, xs) => s"${x} :: ${xs}"
    }
  }
  final case class Cons[+T](head: T, tail: List[T]) extends List[T]
  final case object Nil extends List[Nothing]
  object List {
    def apply[T](elements: T*): List[T] = {
      if (elements.nonEmpty)
        Cons(head = elements.head, tail = List(elements.tail: _*))
      else
        Nil
    }
  }
  println(
    """sealed trait List[+T]
      |final case class Cons[+T](head: T, tail: List[T]) extends List[T]
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
  // In Scala functions are contravariant in their parameters and covariant in their result.
  // This means that if a method expects a function that accepts elements of type T,
  // a function that accepts elements of type U can always be used instead.
  // This makes sense, since a function does is to consume elements.
  // And under the assumption that they are total,
  // if a function is capable of handling any element of type U,
  // then it must be able to handle any element of type T.
  println("-- Contravariance --")
  println(
    """sealed trait List[+T] {
      |  def map[R](f: T => R): List[R] = this match {
      |    case Nil         => Nil
      |    case Cons(x, xs) => Cons(f(x), xs.map(f))
      |  }
      |}""".stripMargin
  )
  def petToName(pet: Pet): String = pet.name
  println(s"Given petToName = (pet: Pet) => pet.name\t->\tdogs.map(petToName) = ${dogs.map(petToName)}")

  // Invariance!
  // Any kind of mutable structure has to be invariant,
  // meaning it is neither covariant nor contravariant.
  // This makes sense, because a mutable structure
  // works both as a producer and as a consumer of elements.
  println("-- Invariance --")
  final class MutableBox[T](initialValue: T) {
    private[this] var value: T = initialValue
    def getValue: T = value
    def setValue(newValue: T): Unit = this.value = newValue
    override def toString: String = s"Box { value = ${value} }"
  }
  println(
    """final class MutableBox[T](initialValue: T) {
      |  private[this] var value: T = initialValue
      |  def getValue: T = value
      |  def setValue(newValue: T): Unit = this.value = newValue
      |  def toString: String = 'Box { value = $value }'
      |}""".stripMargin
  )
  val intBox = new MutableBox(initialValue = 10)
  println(s"Given intBox = new MutableBox(initialValue = 10)\t->\t${intBox}")
  intBox.setValue(newValue = 5)
  println(s"intBox.setValue(newValue = 5) = ${intBox}")
  // At first sight, it looks like this MutableBox could be covariant...
  // But, any mutable data structure has to be invariant!
  // Mutability could lead to runtime type errors,
  // to show how consider the following code (Assuming covariance):
  // val dogBox: MutableBox[Dog] = new MutableBox(initialValue = Dog(name = "Lucas"))
  // val petBox: MutableBox[Pet] = dogBox
  // petBox.setValue(newValue = Cat(name = "Luzy"))
  // val dog: Dog = dogBox.getValue
  // The above line is correct given the type system,
  // but it does not makes sense because now,
  // the value of dogBox is not longer a Dog but a Cat.
  // Hopefully, scala would not let this piece of code compile.
  // Because, MutableBox can not be made covariant in a first place.
  // Note: In Java, every array is covariant and every generic is invariant (by default).
  //       Given that, the above code could be written in Java using Arrays instead of MutableBox,
  //       the code will compile and fail in runtime.
  println()

  // Type bounds!
  // Type bound can be used when defining a generic class or a generic method
  // to limit the possible values to be used in the generic.
  println("- Type bounds -")

  // Upper type bounds!
  // As the name suggest, these are used to limit the generic type parameter
  // to be a subtype of other type.
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
  // and the type bound is unnecessary,
  // since we can just say that the pet argument has to be of type Pet.
  // However, there is a big advantage of using the the P generic.
  // And it is that, the pet value does not forget what is its type.
  // Thus, when accessing it again, we can safely store it in a variable of type Dog,
  // which could not be done if it were upcasted to Pet.

  // Lower type bounds!
  // As the name suggest, these are used to limit the generic parameter
  // to be a supertype of other type.
  // At first it seems that it is not useful at all,
  // however it does has its utility when used together with other type parameters.
  // For example, lets add a :: method to our List,
  // that allows us to prepend an element to the List.
  // At first we may think we could just write `def ::(elem: T): List[T]`.
  // However, this program does not compile,
  // because the parameter elem in :: is of type T, which we declared covariant,
  // and functions are contravariant in their parameter types.
  // To fix this, we need to flip the variance of the type of the parameter element in add,
  // we do this by introducing a new type parameter U that has T as a lower type bound".
  //  - Reference: https://docs.scala-lang.org/tour/lower-type-bounds.html
  // In a more intuitive way, if you add something to a List, then:
  //   * It must be an T.
  //       In this case the List is still a List[T].
  //   * Or it must be any subtype of T.
  //       In this case the element gets upcasted to T, and the List remains a List[T].
  //   * Or if it is another type U, which must be a supertype of T
  //       In this case the List gets upcasted to a List[U].
  //
  //     Note: Because Any is just a supertype of everything,
  //           in the worst case the List will be upcasted to List[Any].
  println("-- Lower type bounds--")
  println(
    """sealed trait List[+T] {
      |  def ::[U >: T](elem: U): List[U] =
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
  // In those cases, type bounds may not work well and we would need a more powerful constructor,
  // in those cases one can use the generalized type constraints.
  // There are two constraints:
  //   * Subtype constraint, T <:< U, proves that T is a subtype of U.
  //   * Sametype constraint, T =:= U, proves that T is the same type as U.
  // They work as an implicit evidence that the compiler will provide.
  // Note: For more information read the "Generalized type constraints in Scala" blog.
  println("- Generalized type constraint -")
  object TypeChecker {
    def isSubType[T, U](implicit ev: T <:< U = null): Boolean =
      Option(ev).fold(ifEmpty = false)(_ => true)
    def isSameType[T, U](implicit ev: T =:= U = null): Boolean =
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
  println(s"Int subtype  of String is ${TypeChecker.isSubType[Int, String]}")
  println(s"Dog subtype  of Pet    is ${TypeChecker.isSubType[Dog, Pet]}")
  println(s"Dog sametype of Pet    is ${TypeChecker.isSameType[Dog, Pet]}")
  println(s"Int sametype of Int    is ${TypeChecker.isSameType[Int, Int]}")
}
