import play.PlayScala
import spray.http.DateTime

name := """questd"""

version := "0.40.12-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

scalacOptions += "-feature"

libraryDependencies ++= Seq(
  cache,
  ws
)

sources in (Compile,doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.11"

libraryDependencies += "org.json4s" %% "json4s-ext" % "3.2.11"

libraryDependencies += "se.radley" %% "play-plugins-salat" % "1.5.0"

// resolvers += "theatr.us" at "http://repo.theatr.us"

libraryDependencies += "us.theatr" %% "akka-quartz" % "0.3.0-SNAPSHOT"

libraryDependencies += "com.restfb" % "restfb" % "1.14.0"

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19"

libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.0.0"

// libraryDependencies += "javax.mail" % "javax.mail-api" % "1.5.4"

libraryDependencies += "javax.mail" % "mail" % "1.4.7"

libraryDependencies += "com.notnoop.apns" % "apns" % "1.0.0.Beta6"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4-M2"



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




