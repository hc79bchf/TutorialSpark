package dader.hongfei.GTstreaming

import akka.actor.Actor
import akka.actor.IO
import akka.actor.IOManager
import akka.actor.Props
import akka.util.ByteString

import org.apache.spark.{SparkConf, Logging, Accumulator}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming._
import org.apache.spark.SparkContext 
import org.apache.spark.SparkContext._
import org.apache.spark.streaming.dstream.{DStream}
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.util.IntParam
import org.apache.spark.util.Utils
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.receiver.{ActorHelper, Receiver}
import org.apache.spark.streaming.util.ManualClock
//import org.apache.spark.streaming.scheduler

import java.io.{InputStreamReader, BufferedReader, InputStream}
import java.net.Socket
import java.io.{File, BufferedWriter, OutputStreamWriter}
import java.net.{InetSocketAddress, SocketException, ServerSocket}
import java.nio.charset.Charset
import java.util.concurrent.{Executors, TimeUnit, ArrayBlockingQueue}
import java.util.concurrent.atomic.AtomicInteger

import scala.collection.mutable.{SynchronizedBuffer, ArrayBuffer, SynchronizedQueue}
import scala.math._
import scala.util.control.Breaks._

import com.google.common.io.Files
import org.scalatest.BeforeAndAfter

import org.apache.hadoop.io.compress.DefaultCodec


/**
 * Counts words in UTF8 encoded, '\n' delimited text received from the network every second.
 *
 * Usage: NetworkWordCount <hostname> <port>
 * <hostname> and <port> describe the TCP server that Spark Streaming would connect to receive data.
 *
 * To run this on your local machine, you need to first run a Netcat server
 *    `$ nc -lk 9999`
 * and then run the example
 *    `$ bin/run-example org.apache.spark.examples.streaming.NetworkWordCount localhost 9999`
 */
 
object NetworkStream_hc79b {

  def findfixation(input: String, dist_user_moniter: Int, maxAngularspeed: Int, moniterwidth: Int, moniterreswidth: Int) = {
  	val timeElapsed = 0.5
	val arr_raw = input.split(";")
	val state = 1 //fixation state, initialize to 1
	for(i<- 0 until arr_raw.length-1){ //here we loop from 0 to arr_raw.length-2, i+1!!!
		var arr_coordinate = arr_raw(i).split("-")
		val cor_x1 = arr_coordinate(0).toInt
		val cor_y1 = arr_coordinate(1).toInt
		val timestamp1 = arr_coordinate(2)
		
		arr_coordinate = arr_raw(i+1).split("-")
		val cor_x2 = arr_coordinate(0).toInt
		val cor_y2 = arr_coordinate(1).toInt
		val timestamp2 = arr_coordinate(2)
		
		val dist_pixels = sqrt(scala.math.pow((cor_x1-cor_x2),2)+scala.math.pow((cor_y1-cor_y2),2))			
		val dist_mm = dist_pixels * moniterwidth / moniterreswidth
		
		var distDegrees = atan(dist_mm / 10 / dist_user_moniter);
		distDegrees = distDegrees * 180 / Pi;
		
		val angularVelocity = distDegrees / timeElapsed;
		if(angularVelocity > maxAngularspeed){
			val state = 0 //non fixation point
			break;
		}		
		println("i="+i+" string="+arr_raw(i)+" x="+cor_x1+" y="+cor_y1+" timestamp="+timestamp1)	
		if(i==arr_raw.length-2){
			println("i="+(i+1)+" string="+arr_raw(i+1)+" x="+cor_x2+" y="+cor_y2+" timestamp="+timestamp2)
		}		
	}
	
	var centroid_x = 0
	var centroid_y = 0
	
	if(state == 1){		
		for(i<- 0 until arr_raw.length){ //loop from 0 to total count arr_raw arr_raw.length-1
			val arr_coordinate = arr_raw(i).split("-")
			val cor_x = arr_coordinate(0).toInt
			val cor_y = arr_coordinate(1).toInt
			centroid_x += cor_x
			centroid_y += cor_y 
		}
		centroid_x = centroid_x/arr_raw.length
		centroid_y = centroid_y/arr_raw.length
		//println("Found fixation: x="+centroid_x+" y="+centroid_y)
	}
	
	(centroid_x,centroid_y)
	
  }
  
  
  def main(args: Array[String]) = {
    if (args.length < 6) {
      System.err.println("Usage: NetworkWordCount <hostname> <distUserMoniter> <maxAngularSpeed> <moniter_width> <moniter_res_width> <ROI file name>")
      System.exit(1)
    }  
    
    val hostname = args(0)
    val distUserMoniter = args(1).toInt
    val maxAngularSpeed = args(2).toInt
    val moniter_width = args(3).toInt
    val moniter_res_width = args(4).toInt
    val ROIfile = args(5)
    
    val testServer = new TestServer()
    testServer.start()
    
    println("Test server port= "+testServer.port)
    
	//-- Call external class's function
    StreamingExamples_hc79b.setStreamingLogLevels()

    //-- Create the context with a 1 second batch size
    val sparkConf = new SparkConf().setAppName("NetworkWordCount")
    val sc = new SparkContext(sparkConf)
    val ROICorfile = sc.textFile(ROIfile)
    val ROIpairs =ROICorfile.map(s =>(s,1))
    ROIpairs.cache()
    
    //-- Loop through each ROI and map fixation to ROI ID
    ROIpairs.foreach(rdd=>{
    	val arr_points = rdd._1.split(";").toList
    	val arr_pairs = (arr_points.last :: arr_points).sliding(2,1).toList
		println(arr_pairs)
		
		//arr_pairs.count { case Seq(pi, pj) => (p) } % 2 == 1
		    
    })
    
    println("------------------------------------------")
    
    val ssc = new StreamingContext(sparkConf, Milliseconds(500))
	
    val lines = ssc.socketTextStream(hostname, testServer.port, StorageLevel.MEMORY_AND_DISK_SER)    
	
	//-- sliding windows: function, window length, slide interval -- //
	val windowedRaw = lines.map(x => (1, x)).reduceByKeyAndWindow((a:String,b:String) => (a +";"+ b), Milliseconds(2500), Milliseconds(500))
	
	windowedRaw.foreach(rdd=>{
		//println("\nStreaming String result:"+rdd.take(1).mkString("\n"))
		val collected = rdd.collect()
		collected.foreach(x=>{		
			println("Loop Result: "+x._2)
			val centroid = findfixation(x._2, distUserMoniter, maxAngularSpeed, moniter_width, moniter_res_width)
			println("fixation x="+centroid._1 + " fixation y="+centroid._2)
		})			
	}) 
    
    ssc.start()    
    val pixel_x = Seq(1 to 3000)
    val pixel_y = Seq(1 to 3000)
    val timestamp = Seq(1 to 3000)
    val expectedOutput = pixel_x.map(_.toString)
    
    Thread.sleep(1000)
    for (i <- 0 to 300) {
      testServer.send(i+"-"+(i+1)+"-"+(i+2)+"\n") //Data Sent by Test Server in following format: x|y|timestamp
      Thread.sleep(500)
    }
    Thread.sleep(1000)
    
    println("Stopping server")
    testServer.stop()
    println("Stopping context")
    ssc.stop()
    
    //ssc.awaitTermination()
  }
}


/** This is a server to test the network socket input stream */
class TestServer(portToBind: Int = 0) extends Logging {

  val queue = new ArrayBlockingQueue[String](100)

  val serverSocket = new ServerSocket(portToBind)

  val servingThread = new Thread() {
    override def run() {
      try {
        while(true) {
          println("Accepting connections on port " + port)
          val clientSocket = serverSocket.accept()
          println("New connection")
          try {
            clientSocket.setTcpNoDelay(true)
            val outputStream = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream))

            while(clientSocket.isConnected) {
              val msg = queue.poll(100, TimeUnit.MILLISECONDS)
              if (msg != null) {
                outputStream.write(msg)
                outputStream.flush()
                //println("Message '" + msg + "' sent")
              }
            }
          } catch {
            case e: SocketException => logError("TestServer error", e)
          } finally {
            println("Connection closed")
            if (!clientSocket.isClosed) clientSocket.close()
          }
        }
      } catch {
        case ie: InterruptedException =>

      } finally {
        serverSocket.close()
      }
    }
  }

  def start() { servingThread.start() }

  def send(msg: String) { queue.put(msg) }

  def stop() { servingThread.interrupt() }

  def port = serverSocket.getLocalPort //local port 
  //def port = 9999
}

/*
class TestOutputStream[T: ClassTag](parent: DStream[T],
    val output: ArrayBuffer[Seq[T]] = ArrayBuffer[Seq[T]]())
  extends foreachRDD[T](parent, (rdd: RDD[T], t: Time) => {
    val collected = rdd.collect()
    output += collected
  }) {

  // This is to clear the output buffer every it is read from a checkpoint
  @throws(classOf[IOException])
  private def readObject(ois: ObjectInputStream) {
    ois.defaultReadObject()
    output.clear()
  }
}
*/