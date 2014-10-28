name := """blog2"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  anorm,
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
  "org.sorm-framework" % "sorm" % "0.3.15",
  "org.slf4j" % "slf4j-simple" % "1.7.2",
  "eu.henkelmann" % "actuarius_2.10.0" % "0.2.6"
)

