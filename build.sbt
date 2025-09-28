lazy val root =
  project
    .in(file("."))
    .settings(
      // Project settings.
      name := "Scala Functional Programming",
      organization := "co.edu.eafit.dis.progfun",
      scalaVersion := "3.3.6",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core" % "2.13.0"
      )
    )
