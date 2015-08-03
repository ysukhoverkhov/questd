
name := """questd"""

version := "0.40.10-SNAPSHOT"

lazy val root = (project in file(".")).
  enablePlugins(PlayScala).
  enablePlugins(BuildInfoPlugin)

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


// fork in run := true

buildInfoKeys := Seq[BuildInfoKey](
  name,
  version,
  buildInfoBuildNumber,
  scalaVersion,
  sbtVersion
  )

buildInfoOptions += BuildInfoOption.BuildTime

buildInfoPackage := "misc"



