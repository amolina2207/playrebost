name := """elrebost"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

lazy val playVersion = play.core.PlayVersion.current

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.0",
  ws % Test, // only used in tests right now
  //"org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.softwaremill.macwire" %% "macros" % "2.2.2" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.2.2",
  "com.softwaremill.macwire" %% "proxy" % "2.2.2",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.11.11",
  cache,
  filters,
  ws,
  "jp.t2v" %% "stackable-controller" % "0.6.0"
)

fork in run := false
fork in test := false