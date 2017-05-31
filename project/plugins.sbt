// The Typesafe repository
resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots repository" at "http://repo.typesafe.com/typesafe/snapshots/"
)

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.2")
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-language:_")
