import play.PlayScala

name := """questd"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.5"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  cache,
  ws
)


libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"

libraryDependencies += "se.radley" %% "play-plugins-salat" % "1.5.0"

// resolvers += "theatr.us" at "http://repo.theatr.us"

libraryDependencies += "us.theatr" %% "akka-quartz" % "0.3.0-SNAPSHOT"

libraryDependencies += "com.restfb" % "restfb" % "1.8.0"

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.8.0"


fork in run := true