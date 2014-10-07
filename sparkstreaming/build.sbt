import sbt._
import Process._
import Keys._

name := "SparkStreaming"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.0.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming" % "1.0.2",
  "org.apache.spark" %% "spark-streaming-twitter" % "1.0.2",
  "org.scalatest" % "scalatest_2.10" % "2.0"
)

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"

