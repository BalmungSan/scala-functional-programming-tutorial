// In Scala, every expression / value has a type associated with it.
// And the compiler is able to infer them.
2
// But you can always specify it.
2: Double

// All values are objects, and all operations are just method calls.
2 + 2 // This is sugar syntax.
2.+(2) // This is what the compilers actually see.

// Expressions / Values / Objects can be assigned to variables so you can reference them later.
val x: Int = 1
x
// Again, you can let the compiler infer the type:
val y = 3 // Hover your mouse over the variable name to see the inferred type.
y * y

// A variable declaration is composed of 4 parts
// <modifier> <name> [: <type>] = <expression>
// val        z       : Int     = 5
val z: Int = 5
// There are 4 modifiers:
// val, var, def, lazy val.

// Val: Immutable, Eager, Cached.
// Val variables are evaluated as soon as they are defined and can't be mutated.
println("----------")
println("Before defining a val")
val aVal = {
  println("Evaluating a val")
  "foo"
} // Print happens here.
println("After defining a val")
println("Before first use of a val")
aVal // No print here.
println("After first use of a val")
println("Before second use of a val")
aVal // No print here.
println("After second use of a val")
// You can't mutate it.
// The next line wouldn't compile if you uncomment it:
// aVal = "bar"
println("----------")

// Var: Mutable, Eager, Cached.
// Var variables are evaluated as soon as they are defined and can be mutated.
println("----------")
println("Before defining a var")
var aVar = {
  println("Evaluating a var")
  "bar"
} // Print happens here.
println("After defining a var")
println("Before first use of a var")
aVar // No print here.
println("After first use of a var")
println("Before second use of a var")
aVar // No print here.
println("After second use of a var")
// You can mutate it:
aVar = "foo"
aVar
// But you can't change its type.
// The next line wouldn't compile if you uncomment it:
// aVar = 10
println("----------")

// Def: Immutable, Lazy, Volatile.
// Def variables are not evaluated when defined, but rather when used,
// and they will always re-evaluate on each use.
println("----------")
println("Before defining a def")
def aDef = {
  println("Evaluating a def")
  "baz"
} // No print here.
println("After defining a def")
println("Before first use of a def")
aDef // Print happens here.
println("After first use of a def")
println("Before second use of a def")
aDef // Print happens here.
println("After second use of a def")
// You can't mutate it.
// The next line wouldn't compile if you uncomment it:
// aDef = "foo"
println("----------")

// Lazy Val: Immutable, Lazy, Cached.
// Lazy Val variables are not evaluated when defined, but rather when first used,
// and then they will remember the result and behave like a normal val.
println("----------")
println("Before defining a lazy val")
lazy val aLazyVal = {
  println("Evaluating a lazy val")
  "quax"
} // No print here.
println("After defining a lazy val")
println("Before first use of a lazy val")
aLazyVal // Print happens here.
println("After first use of a lazy val")
println("Before second use of a lazy val")
aLazyVal // No print here.
println("After second use of a lazy val")
// You can't mutate it.
// The next line wouldn't compile if you uncomment it:
// aLazyVal = "foo"
// Lazy vals have a cost because of concurrency, they need to ensure only one thread initializes it.
// Thus, they have a lock, which makes all further access slower than a regular val.
println("----------")
