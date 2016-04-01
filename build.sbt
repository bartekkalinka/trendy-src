name := "trendy-src"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    "org.scalatest" %% "scalatest" % "2.2.5" % "test",
    "com.typesafe.slick" %% "slick" % "3.1.1",
    "com.github.tminglei" %% "slick-pg" % "0.12.0",
    "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
    "com.typesafe" % "config" % "1.3.0"
  )
}
