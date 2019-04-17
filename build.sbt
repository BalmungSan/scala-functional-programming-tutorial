// Project settings.
ThisBuild / name := "Scala Functional Programming"
ThisBuild / organization := "co.edu.eafit.dis.progfun"
ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.12.8"
ThisBuild / scalacOptions ++= Seq(
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

// Dependencies.
val CatsVersion = "1.6.0"
val CatsEffectVersion = "1.2.0"
ThisBuild / libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core"   % CatsVersion,
  "org.typelevel" %% "cats-effect" % CatsEffectVersion
)

lazy val commonSettings = Seq(
  // Disable the linter and warning flags on the console.
  Compile / console / scalacOptions ~= {
    _.filterNot(flag => flag.startsWith("-Xlint") || flag.startsWith("-Ywarn"))
  },

   // Kind projector.
  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),

  // Better monadic for.
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0"),

  // Ammonite.
  libraryDependencies += "com.lihaoyi" % "ammonite" % "1.6.5" % Test cross CrossVersion.full,

  Test / sourceGenerators += Def.task {
    val file = (Test / sourceManaged).value / "amm.scala"
    IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
    Seq(file)
  }.taskValue,

  Test / fullClasspath ++= {
    (updateClassifiers in Test).value
      .configurations
      .find(_.configuration == Test.name)
      .get
      .modules
      .flatMap(_.artifacts)
      .collect { case (a, f) if a.classifier == Some("sources") => f }
  }
)

// Alias for executing ammonite.
addCommandAlias("amm", "test:runMain amm")

// Modules.
lazy val scalaintro = project.settings(commonSettings)
lazy val catsintro  = project.settings(commonSettings)
lazy val catscases  = project.settings(commonSettings)
lazy val iointro    = project.settings(commonSettings)
