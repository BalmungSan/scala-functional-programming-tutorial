// Project settings.
ThisBuild / name := "Scala Functional Programming"
ThisBuild / organization := "co.edu.eafit.dis.progfun"
ThisBuild / scalaVersion := "2.13.0"

// Dependencies.
val CatsVersion = "2.0.0-RC2"
val CatsEffectVersion = "2.0.0-RC2"

lazy val commonSettings = Seq(
  // Kind projector.
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),

  // Better monadic for.
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0"),

  // Ammonite.
  libraryDependencies += "com.lihaoyi" % "ammonite" % "1.6.9" % Test cross CrossVersion.full,

  Test / sourceGenerators += Def.task {
    val file = (Test / sourceManaged).value / "amm.scala"
    IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
    Seq(file)
  }.taskValue,

  Test / fullClasspath ++= {
    (updateClassifiers in Test).value.configurations
      .find(_.configuration == Test.name)
      .get
      .modules
      .flatMap(_.artifacts)
      .collect { case (a, f) if a.classifier == Some("sources") => f }
  }
)

// Root project.
lazy val root = (project in file("."))
  .aggregate(
    scalaIntro,
    catsIntro,
    catsCases,
    ioIntro,
    ioTutorial
  )

// Modules.
lazy val scalaIntro = (project in file("scala-intro"))
  .settings(commonSettings)

lazy val catsIntro = (project in file("cats-intro"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % CatsVersion
    )
  )

lazy val catsCases = (project in file("cats-cases"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % CatsVersion
    )
  )
lazy val ioIntro = (project in file("io-intro"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    )
  )

lazy val ioTutorial = (project in file("io-tutorial"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % CatsVersion,
      "org.typelevel" %% "cats-effect" % CatsEffectVersion
    )
  )
