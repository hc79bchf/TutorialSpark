sbt -Dsbt.ivy.home=../sbt/ivy package

#Needs local[2] instead of local in order to enable concurrent processings
$SPARK_HOME/bin/spark-submit  \
     --class "dader.hongfei.gazetrackingstream.GazeTrackingStream_hc79b" \
     --master local[2] \
     target/scala-2.10/sparkstreaming_2.10-1.0.jar localhost 70 20 500 1920 hdfs://slave1:8020/user/hc79b/inputspark/testROI.txt
     
     #### arguments: hostname, distance user to moniter, max angular speed, moniter width, moniter resolutin width, ROI file name
     #### ROI file format x-y;x-y, the last point is not the same as first point!!!
     