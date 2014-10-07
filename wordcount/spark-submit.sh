mvn package
source /etc/spark/conf/spark-env.sh

# system jars:
CLASSPATH=/etc/hadoop/conf
CLASSPATH=$CLASSPATH:$HADOOP_HOME/*:$HADOOP_HOME/lib/*
CLASSPATH=$CLASSPATH:$HADOOP_HOME/../hadoop-mapreduce/*:$HADOOP_HOME/../hadoop-mapreduce/lib/*
CLASSPATH=$CLASSPATH:$HADOOP_HOME/../hadoop-yarn/*:$HADOOP_HOME/../hadoop-yarn/lib/*
CLASSPATH=$CLASSPATH:$HADOOP_HOME/../hadoop-hdfs/*:$HADOOP_HOME/../hadoop-hdfs/lib/*
CLASSPATH=$CLASSPATH:$SPARK_HOME/assembly/lib/*

SPARK_JAR=$SPARK_HOME/assembly/lib/spark-assembly_2.10-0.9.0-cdh5.0.1-hadoop2.3.0-cdh5.0.1.jar	

# app jar:
CLASSPATH=$CLASSPATH:target/sparkwordcount-0.0.1-SNAPSHOT.jar

CONFIG_OPTS="-Dspark.master=local -Dspark.jars=target/sparkwordcount-0.0.1-SNAPSHOT.jar"

java -cp $CLASSPATH $CONFIG_OPTS com.cloudera.sparkwordcount.SparkWordCount hdfs:///user/hc79b/inputspark/inputfile.txt 2
