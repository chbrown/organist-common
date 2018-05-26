lazy val sharedSettings = Seq(
  organization := "org.anist",
  version := "0.1.0",
  licenses := Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  scalaVersion := "2.11.12",
  crossScalaVersions := Seq("2.11.12", "2.12.6"),
  scalacOptions := Seq(
    "-target:jvm-1.8",
    "-deprecation",
    "-unchecked",
    "-feature",
    "-Xfuture"
  )
)

lazy val root = (project in file(".")).settings(skip in publish := true).aggregate(common)
lazy val common = (project in file("common")).settings(sharedSettings)
