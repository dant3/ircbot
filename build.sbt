name := "ircbot"

version := "1.0"

scalaVersion := "2.11.6"

val libs = new {
  val rxScala = "io.reactivex" %% "rxscala" % "0.25.0"
  val scalaParser = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3"

  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % "test"

  def toSeq = Seq(rxScala, scalaTest)
}

libraryDependencies ++= libs.toSeq