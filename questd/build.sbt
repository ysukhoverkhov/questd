import play.PlayScala
import spray.http.DateTime

name := """questd"""

version := "0.40.01-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  cache,
  ws
)


libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"

libraryDependencies += "se.radley" %% "play-plugins-salat" % "1.5.0"

// resolvers += "theatr.us" at "http://repo.theatr.us"

libraryDependencies += "us.theatr" %% "akka-quartz" % "0.3.0-SNAPSHOT"

libraryDependencies += "com.restfb" % "restfb" % "1.9.0"

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "1.8.0"

libraryDependencies += "javax.mail" % "mail" % "1.4.7"


// fork in run := true


buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](
  name,
  version,
  buildInfoBuildNumber,
  BuildInfoKey.action("buildTime") {
    DateTime.now
  },
  scalaVersion,
  sbtVersion
  )

buildInfoPackage := "misc"

