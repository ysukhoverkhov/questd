name := "questd"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)     

libraryDependencies += "se.radley" %% "play-plugins-salat" % "1.3.0"

play.Project.playScalaSettings
