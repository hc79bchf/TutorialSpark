import sbt._
import Process._
import Keys._

name := "scalasimpleapp"

version := "1.0"

scalaVersion := "2.10.4"

libraryDependencies += "org.apache.spark" % "spark-core_2.10" % "1.0.0"

resolvers += "Akka Repository" at "http://repo.akka.io/releases/"



