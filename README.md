# Functional Programming in Scala Tutorial

This repo contains the notes of the functional programming hotbed of research of the _Universidad **EAFIT**_.  
This is intended to be a simple introductory tutorial to **Scala** in general and to Functional Programming
_(using the [Typelevel stack](https://typelevel.org/projects))_ in it.

## Contents

1. `scalaintro` This package contains notes for introduction to the **Scala** Programming Language.
    1. `ScalaNotes.scala` Basic introduction to the language.
    2. `ImplicitsNotes.scala` Introduction to the implicits mechanism of **Scala** _(includes ValueClasses too)_.
    3. `SubtypeNotes.scala` Variance, Type bounds & Generalized type constraints.
    4. `TypeclassesNotes.scala` Simple introduction to the typeclass pattern in **Scala**.
2. `catsintro` This package contains notes for introduction to the **Cats** library for Functional Programming.
    1. `MonoidNotes.scala` Semigroup & Monoid notes.
    2. `FunctorNotes.scala` Covariant, Contravariant & Invariant Functor notes.
    3. `MonadNotes.scala` Monad notes - Id, Error, Eval, Writer, Reader & State Monads notes - Monad Transformers notes.
    4. `ApplicativeNotes.scala` Applicative & Validated notes.
    5. `TraverseNotes.scala` Foldable & Traverse notes.
3. `catscases` This packages contains the solutions to case studies of **Scala with Cats** book. _(pending)_
4. `iointro` This package contains notes for introduction to the **Cats-Effect** library for Effects. _(pending)_

## Bibliography

The following is a list of bibliographic material used for this tutorial.

+ Underscore - **Scala with Cats**, Book. https://underscore.io/books/scala-with-cats
+ Typelevel - **Cats**, Website. https://typelevel.org/cats
+ Typelevel - **Cats Effect**, Website. https://typelevel.org/cats-effect
+ Rob Norris _(tpolecat)_ - **Cats Infographic**, Image. https://github.com/tpolecat/cats-infographic
+ Rob Norris _(tpolecat)_ - **Functional Programming with Effects**, Conference. https://slideslive.com/38908886/functional-programming-with-effects
+ Rob Norris _(tpolecat)_ - **Introduction to Typeclasses in Scala**, Blog. https://tpolecat.github.io/2013/10/12/typeclass.html
+ Scala Lang - **Scala Standard Library - API**, Scaladoc. https://www.scala-lang.org/api/current
+ Scala Lang - **Tour of Scala**, Docs. https://docs.scala-lang.org/tour/tour-of-scala.html
+ Scala Lang - **Where Does Scala Look For Implicits?**, Docs. https://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html
+ Scala Lang - **Value Classes**, Docs. https://docs.scala-lang.org/overviews/core/value-classes.html
+ Scala Lang - **Implicit Classes**, Docs. https://docs.scala-lang.org/overviews/core/implicit-classes.html
+ Daniel C. Sobral - **Difference between method and function in Scala**, StackOverflow Answer. https://stackoverflow.com/a/2530007/4111404
+ Erik Bruchez - **Generalized type constraints in Scala (without a PhD)**, Blog. https://blog.bruchez.name/2015/11/generalized-type-constraints-in-scala.html
+ Scala Community - **A relaxed chat room about all things Scala**, Gitter Channel. https://gitter.im/scala/scala
