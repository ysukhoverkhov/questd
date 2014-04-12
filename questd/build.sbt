name := "questd"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     


libraryDependencies += "se.radley" %% "play-plugins-salat" % "1.4.0"


resolvers += "theatr.us" at "http://repo.theatr.us"

libraryDependencies += "us.theatr" %% "akka-quartz" % "0.2.0"



libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "0.8.0"

play.Project.playScalaSettings

