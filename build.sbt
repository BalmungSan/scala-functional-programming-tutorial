// Project settings.
name := "Scala Functional Programming"
organization := "co.edu.eafit.dis.progfun"
version := "0.1.0"
scalaVersion := "2.12.8"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "utf-8",
  "-explaintypes",
  "-feature",
  "-unchecked",
  "-Dkp:genAsciiNames=true", // For kind-projector.
  "-Xfatal-warnings",
  "-Xlint:constant",
  "-Xlint:inaccessible",
  "-Xlint:infer-any",
  "-Xlint:missing-interpolator",
  "-Xlint:nullary-override",
  "-Xlint:nullary-unit",
  "-Xlint:option-implicit", // Warn if an implicit conversion was applied before a call to Option.apply.
  "-Xlint:package-object-classes",
  "-Xlint:private-shadow",
  "-Xlint:stars-align", // See https://github.com/scala/scala/pull/4216
  "-Xlint:type-parameter-shadow",
  "-Xlint:unsound-match",
  "-Ypartial-unification",
  "-Ywarn-macros:after", // Execute the linter after macro expansion.
  "-Ywarn-dead-code",
  "-Ywarn-extra-implicit",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",
  "-Ywarn-value-discard"
)

// Disable the linter and warning flags on the console.
scalacOptions in (Compile, console) ~= {
  _.filterNot(flag => flag.startsWith("-Xlint") || flag.startsWith("-Ywarn"))
}

// Dependencies.
val CatsVersion = "1.6.0"
val CatsEffectVersion = "1.2.0"
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"   % CatsVersion,
  "org.typelevel" %% "cats-effect" % CatsEffectVersion
)

// Kind projector.
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")

// Ammonite.
libraryDependencies += "com.lihaoyi" % "ammonite" % "1.6.4" % "test" cross CrossVersion.full

sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
  Seq(file)
}.taskValue

(fullClasspath in Test) ++= {
  (updateClassifiers in Test).value
    .configurations
    .find(_.configuration == Test.name)
    .get
    .modules
    .flatMap(_.artifacts)
    .collect{case (a, f) if a.classifier == Some("sources") => f}
}

addCommandAlias("amm", "test:runMain amm")
