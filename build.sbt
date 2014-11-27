name := """ssmu-powerlifting"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "org.apache.commons" % "commons-email" % "1.3.3",
  "commons-validator" % "commons-validator" % "1.4.0",
  "org.mindrot" % "jbcrypt" % "0.3m"
)
