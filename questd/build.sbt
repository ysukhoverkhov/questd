import play.PlayScala

name := """questd"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.4"

libraryDependencies ++= Seq(
  cache,
  ws
)


libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"

libraryDependencies += "se.radley" %% "play-plugins-salat" % "1.5.0"

// resolvers += "theatr.us" at "http://repo.theatr.us"

libraryDependencies += "us.theatr" %% "akka-quartz" % "0.3.0-SNAPSHOT"

libraryDependencies += "com.restfb" % "restfb" % "1.7.0"

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.10"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.6.0"
