//project settings
name := "intro-cats"
organization := "co.edu.eafit.dis.progfun"
version := "0.1.0"
scalaVersion := "2.12.6"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "utf-8",
  "-explaintypes",
  "-feature",
  "-unchecked",
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
)

val CatsVersion = "1.4.0"
libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % CatsVersion
)
