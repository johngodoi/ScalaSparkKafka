# ScalaSparkKafka

It just loads data to kafka through spark and reads it back.

This repository is partially based in those tutorials:
* [kafka on docker](https://medium.com/trainingcenter/apache-kafka-codifica%C3%A7%C3%A3o-na-pratica-9c6a4142a08f)
* [kafka and spark on azure](https://docs.microsoft.com/pt-br/azure/hdinsight/hdinsight-apache-kafka-spark-structured-streaming)
* [kafka and pyspark locally](https://github.com/PritomDas/Real-Time-Streaming-Data-Pipeline-and-Dashboard/blob/13e2f4e6cb19f61d82cff053aa63e572d5e55a29/datamaking_real_time_data_pipeline%20(PySpark)/real_time_streaming_data_pipeline.py)

## Requirements

In order to execute this code, you are going to need:

* sbt
* Java 8+
* docker
* on Windows:
    * setup winutil.exe and hadoop.dll, like [here](https://sparkbyexamples.com/spark/spark-hadoop-exception-in-thread-main-java-lang-unsatisfiedlinkerror-org-apache-hadoop-io-nativeio-nativeiowindows-access0ljava-lang-stringiz/).

## Setting up services
```shell
docker-compose up -d #starting kafka
```

```shell
docker-compose ps #checking  if expected services are running
```

```shell
docker-compose logs zookeeper | grep -i binding #check logs from zookeeper
docker-compose logs kafka | grep -i started #check logs from kafka
```

## Test drive

```shell
# creating a new topic
docker-compose exec kafka kafka-topics --create --topic meu-topico-legal --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zookeeper:2181
```

```shell
#checking topic existence
docker-compose exec kafka  kafka-topics --describe --topic meu-topico-legal --zookeeper zookeeper:2181
```

```shell
#Producing 100 messages
docker-compose exec kafka bash -c "seq 100 | kafka-console-producer --request-required-acks 1 --broker-list kafka:9092 --topic meu-topico-legal && echo 'Produced 100 messages.'"
```

```shell
#Consuming 100 messages
docker-compose exec kafka kafka-console-consumer --bootstrap-server kafka:9092 --topic meu-topico-legal --from-beginning --max-messages 100
```