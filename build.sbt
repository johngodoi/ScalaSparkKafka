name := "ScalaSparkKafka"

version := "0.1"

scalaVersion := "2.12.12"

libraryDependencies += "com.amazonaws" % "aws-java-sdk" % "1.10.6"
libraryDependencies += "com.amazonaws" % "aws-java-sdk-s3" % "1.10.6"
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "3.2.1"
libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "3.2.1"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming-kafka-0-10-assembly" % "3.0.0"
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "2.6.0"


