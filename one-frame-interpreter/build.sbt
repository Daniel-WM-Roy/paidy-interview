ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.7.4"

lazy val root = (project in file("."))
  .settings(
    name := "one-frame-interpreter"
  )

libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.1.24",
  "dev.zio" %% "zio-test" % "2.1.24",
  "dev.zio" %% "zio-http" % "3.7.4",
  "dev.zio" %% "zio-config" % "4.0.6",
  "dev.zio" %% "zio-json" % "0.7.45",
  "dev.zio" %% "zio-config-magnolia" % "4.0.6",
  "dev.zio" %% "zio-config-typesafe" % "4.0.6",
  "eu.timepit" %% "refined" % "0.11.3"
)