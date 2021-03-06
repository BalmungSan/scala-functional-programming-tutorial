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
3. `catscases` This packages contains the solutions to case studies of **Scala with Cats** book.
    1. `AsyncTesting.scala` Solution to the "Testing Asynchronous Code" case study.
    2. `MapReduce.scala` Solution to the "Map-Reduce" case study.
    3. `DataValidation.scala` Solution to the "Data Validation" case study.
    4. `CRDT.scala` Solution to the "CRDTs" case study.
4. `iointro` This package contains notes for introduction to the **Cats-Effect** library for encoding side-effects as pure values.
    1. `IONotes.scala` Introduction to the IO Monad.

## Bibliography

The following is a list of bibliographic material used for this tutorial.

+ Underscore - **Scala with Cats**, Book. https://underscore.io/books/scala-with-cats
+ Underscore - **Playing type tetris**, Blog. https://underscore.io/blog/posts/2017/04/11/type-tetris.html
+ Typelevel - **There are more types than classes**. https://typelevel.org/blog/2017/02/13/more-types-than-classes.html
+ Typelevel - **Cats**, Website. https://typelevel.org/cats
+ Typelevel - **Cats Effect**, Website. https://typelevel.org/cats-effect
+ Rob Norris _(tpolecat)_ - **Cats Infographic**, Image. https://github.com/tpolecat/cats-infographic
+ Rob Norris _(tpolecat)_ - **Functional Programming with Effects**, Conference talk. https://slideslive.com/38908886/functional-programming-with-effects
+ Rob Norris _(tpolecat)_ - **Introduction to Typeclasses in Scala**, Blog. https://tpolecat.github.io/2013/10/12/typeclass.html
+ Scala Lang - **Scala Standard Library - API**, Scaladoc. https://www.scala-lang.org/api/current
+ Scala Lang - **Tour of Scala**, Docs. https://docs.scala-lang.org/tour/tour-of-scala.html
+ Scala Lang - **Where Does Scala Look For Implicits?**, Docs. https://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html
+ Scala Lang - **Value Classes**, Docs. https://docs.scala-lang.org/overviews/core/value-classes.html
+ Scala Lang - **Implicit Classes**, Docs. https://docs.scala-lang.org/overviews/core/implicit-classes.html
+ Daniel C. Sobral - **Difference between method and function in Scala**, StackOverflow Answer. https://stackoverflow.com/a/2530007/4111404
+ Erik Bruchez - **Generalized type constraints in Scala (without a PhD)**, Blog. https://blog.bruchez.name/2015/11/generalized-type-constraints-in-scala.html
+ DevInsideYou - **Monads**, Youtube playlist. https://www.youtube.com/playlist?list=PLJGDHERh23x-9bxGrCbyX-tXJG99XczNC
+ Philip Schwarz - **The Monad Fact Slide Deck Series**, Slides. https://www.slideshare.net/pjschwarz/the-monad-fact-slide-deck-series-229474965
+ Gavin Bisesi _(Daenyth)_ - **Intro to Cats-Effect**, Conference talk. https://github.com/daenyth/intro-cats-effect
+ Ryan Peters _(sloshy)_- **Streams - Your New Favorite Primitive**, Conference talk. https://www.youtube.com/watch?v=BZ8O6T7Y1UE
+ Fabio Labella _(SystemFw)_- **All his talks**, Conference talk. https://systemfw.org/talks.html
+ Scala Community - **A relaxed chat room about all things Scala**, Gitter Channel. https://gitter.im/scala/scala
