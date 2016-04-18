name := "trendy-src"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "com.typesafe.slick" %% "slick" % "3.1.1",
    "com.github.tminglei" %% "slick-pg" % "0.12.0",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
    "com.typesafe" % "config" % "1.3.0",
    "joda-time" % "joda-time" % "2.9",
    "org.joda" % "joda-convert" % "1.8.1",
    "com.github.tototoshi" %% "slick-joda-mapper" % "2.0.0"
  )
}

initialCommands in console := "import pl.bka.Api._; import pl.bka._"