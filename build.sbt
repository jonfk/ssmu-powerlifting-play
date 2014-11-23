name := """ssmu-powerlifting"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.sksamuel.elastic4s" %% "elastic4s" % "1.4.0",
  "com.typesafe.play" %% "play-slick" % "0.8.0"
)
