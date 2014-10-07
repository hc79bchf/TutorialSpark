hadoop fs -rm -r simpleappoutput

sbt -Dsbt.ivy.home=../sbt/ivy package

$SPARK_HOME/bin/spark-submit  \
     --class "dader.hongfei.scalasimpleapp.SimpleApp_hc79b" \
     --master spark://'slave1':7077 \
     target/scala-2.10/scalasimpleapp_2.10-1.0.jar simpleappoutput