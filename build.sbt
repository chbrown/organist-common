organization := "org.anist"

name := "common"

version := "0.1.0"

licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

scalaVersion := "2.11.12"
crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.6")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:postfixOps"
)
