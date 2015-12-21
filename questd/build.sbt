
name := """questd"""

version := "0.50.03-SNAPSHOT"

lazy val root = (project in file(".")).
  enablePlugins(PlayScala).
  enablePlugins(BuildInfoPlugin)

scalaVersion := "2.11.7"

ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

scalacOptions += "-feature"

scalacOptions in Test += "-Yrangepos"


sources in (Compile,doc) := Seq.empty

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false


testOptions in Test += Tests.Argument("xonly", "console")


resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"


libraryDependencies ++= Seq(
  cache,
  ws
)

libraryDependencies += "org.json4s" %% "json4s-native" % "3.2.9"

libraryDependencies += "org.json4s" %% "json4s-ext" % "3.2.9"

libraryDependencies += "se.radley" %% "play-plugins-salat" % "1.5.6"

libraryDependencies += "us.theatr" %% "akka-quartz" % "0.3.0-SNAPSHOT"

libraryDependencies += "com.restfb" % "restfb" % "1.17.0"

// libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.2.0"

// libraryDependencies += "javax.mail" % "javax.mail-api" % "1.5.4"

libraryDependencies += "javax.mail" % "mail" % "1.4.7"

libraryDependencies += "com.notnoop.apns" % "apns" % "1.0.0.Beta6"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.3.13"


libraryDependencies += specs2 % Test

libraryDependencies += "org.mockito" % "mockito-all" % "1.10.19" % "test"


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

