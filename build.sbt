name := """blog2"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, SbtWeb)

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

LessKeys.compress in Assets := true

scalaVersion := "2.11.5"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc41",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "org.joda" % "joda-convert" % "1.6",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "org.slf4j" % "slf4j-simple" % "1.7.2",
  "org.webjars" % "bootstrap" % "3.0.2",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test",
  "org.scalatestplus" %% "play" % "1.2.0" % "test"
)

fork in Test := true

javaOptions in Test := Seq("-Denv=TEST")

