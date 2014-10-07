/*** SimpleApp.scala ***/

package dader.hongfei.scalasimpleapp

import org.apache.spark.SparkContext 
import org.apache.spark.SparkContext._

object SimpleApp_hc79b {
	def main(args: Array[String]) {
		val logFile = "./inputspark/README.md" // Should be some file on your system! 
		val sc = new SparkContext("local", "Simple App", "SPARK_HOME",
			List("target/scala-2.10/scalasimpleapp_2.10-1.0.jar"))
		val logData = sc.textFile(logFile, 2).cache()	
		
		val linea = logData.filter(line => line.contains("a"))
		val numAs = logData.filter(line => line.contains("a")).count()
		val numBs = logData.filter(line => line.contains("b")).count()
		
		linea.saveAsTextFile(args(0)) 
		
		println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
	}
}