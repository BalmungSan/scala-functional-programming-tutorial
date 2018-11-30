// Project settings.
name := "Scala Functional Programming"
organization := "co.edu.eafit.dis.progfun"
version := "0.1.0"
scalaVersion := "2.12.7"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "utf-8",
  "-explaintypes",
  "-feature",
  "-unchecked",
  "-language:higherKinds",
  "-Xlint:infer-any",
  "-Xlint:unsound-match",
  "-Ypartial-unification",
  "-Ywarn-macros:after",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-unused:implicits",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:params",
  "-Ywarn-unused:patvars",
  "-Ywarn-unused:privates",
  "-Ywarn-value-discard",
  "-Dkp:genAsciiNames=true"
)

// Dependencies.
val CatsVersion = "1.4.0"
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % CatsVersion
)

// Kind projector.
addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")

// Ammonite.
libraryDependencies += "com.lihaoyi" % "ammonite" % "1.3.2" % "test" cross CrossVersion.full

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
