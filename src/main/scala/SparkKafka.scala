import org.apache.spark.sql.SparkSession

object SparkKafka extends App {

  cleanUpPath("./example")
  cleanUpPath("./streamcheckpoint")

  private def cleanUpPath(path:String) = {
    import scala.reflect.io.Directory
    import java.io.File

    val directory = new Directory(new File(path))
    directory.deleteRecursively()
  }

  private val spark: SparkSession = SparkSession
    .builder()
    .appName("SparkXKafka")
    .master("local[*]")
  .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")
  import spark.implicits._

  // Load the data from the New York City Taxi data REST API for 2016 Green Taxi Trip Data
  val url="https://data.cityofnewyork.us/resource/pqfs-mqru.json"
  val result = scala.io.Source.fromURL(url).mkString

  // Create a dataframe from the JSON data
  val taxiDF = spark.read.json(Seq(result).toDS)

  // Display the dataframe containing trip data
  taxiDF.show()

  val kafkaBrokers="localhost:9092"
  val kafkaTopic="tripdata"

  println("Finished setting Kafka broker and topic configuration.")

  // Select the vendorid as the key and save the JSON string as the value.
  val query = taxiDF
    .selectExpr("CAST(vendorid AS STRING) as key", "to_JSON(struct(*)) AS value")
    .write
    .format("kafka")
    .option("kafka.bootstrap.servers", kafkaBrokers)
    .option("topic", kafkaTopic)
    .save()

  println("Data sent to Kafka")


  // Import bits useed for declaring schemas and working with JSON data
  import org.apache.spark.sql.functions._
  import org.apache.spark.sql.types._

  // Define a schema for the data
  val schema = (new StructType).add("dropoff_latitude", StringType)
    .add("dropoff_longitude", StringType)
    .add("extra", StringType)
    .add("fare_amount", StringType)
    .add("improvement_surcharge", StringType)
    .add("lpep_dropoff_datetime", StringType)
    .add("lpep_pickup_datetime", StringType)
    .add("mta_tax", StringType)
    .add("passenger_count", StringType)
    .add("payment_type", StringType)
    .add("pickup_latitude", StringType)
    .add("pickup_longitude", StringType)
    .add("ratecodeid", StringType)
    .add("store_and_fwd_flag", StringType)
    .add("tip_amount", StringType)
    .add("tolls_amount", StringType)
    .add("total_amount", StringType)
    .add("trip_distance", StringType)
    .add("trip_type", StringType)
    .add("vendorid", StringType)

  println("Schema declared")

  // Read a batch from Kafka
  val kafkaDF = spark.read.format("kafka").option("kafka.bootstrap.servers", kafkaBrokers).option("subscribe", kafkaTopic).option("startingOffsets", "earliest").load()

  // Select data and write to file
  val queryRead = kafkaDF.select(from_json(col("value").cast("string"), schema) as "trip").cache()
  queryRead.write.format("parquet").option("path","./example/batchtripdata").option("checkpointLocation", "./batchcheckpoint").save()
  queryRead.show()
  println("Wrote data to file")

  // Stream from Kafka
  val kafkaStreamDF = spark.readStream.format("kafka").option("kafka.bootstrap.servers", kafkaBrokers).option("subscribe", kafkaTopic).option("startingOffsets", "earliest").load()

  // Select data from the stream and write to file
  kafkaStreamDF.select(from_json(col("value").cast("string"), schema) as "trip").writeStream.format("parquet").option("path","./example/streamingtripdata").option("checkpointLocation", "./streamcheckpoint").start.awaitTermination(30000)

  println("Wrote data to file")

  spark.stop()

}
