package controllers

import javax.inject.Singleton

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.util.Bytes.toBytes
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import play.api.mvc.{Action, Controller}
//import org.apache.spark.rdd
import org.apache.spark.sql.{ DataFrame, SparkSession }
//import org.apache.spark._
//import org.apache.spark.rdd.NewHadoopRDD
//import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor}
//import org.apache.hadoop.hbase.client.HBaseAdmin
//import org.apache.hadoop.hbase.mapreduce.TableInputFormat
//import org.apache.hadoop.fs.Path;
//import org.apache.hadoop.hbase.HColumnDescriptor
//import org.apache.hadoop.hbase.util.Bytes
//import org.apache.hadoop.hbase.client.Put;
//import org.apache.hadoop.hbase.client.HTable;
//import org.apache.hadoop.hbase.mapred.TableOutputFormat
//import org.apache.hadoop.mapred.JobConf
//import org.apache.hadoop.hbase.io.ImmutableBytesWritable
//import org.apache.hadoop.mapreduce.Job
//import org.apache.hadoop.mapreduce.lib.input.FileInputFormat
//import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat
//import org.apache.hadoop.hbase.KeyValue
//import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat
//import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles

/**
 * version listings is defined using the BuildInfo feature
 */
@Api("Utils")
@Singleton
class VersionController extends Controller {

  // public api
  @ApiOperation(
    value = "Version List",
    notes = "Provides a full listing of all versions of software related tools - this can be found in the build file.",
    httpMethod = "GET"
  )
  @ApiResponses(Array(
    new ApiResponse(code = 200, message = "Success - Displays a version list as json.")
  ))
  def version = Action {
    //Create a SparkContext to initialize Spark
    val sparkSession: SparkSession = SparkSession.builder
      .master("local")
      .appName("example")
      .getOrCreate()

    val sc: SparkContext = sparkSession.sparkContext
    val config: Configuration = HBaseConfiguration.create
    //    val hbaseContext = new HBaseContext(sc, config)

    //    val load = new LoadIncrementalHFiles(config)

    // environment vars
    val columnFamily: String = "d"
    val tableName: String = "ch-data"
    val stagingFolder: String = "conf/tmp/"

    val chDF = sparkSession
      .read.option("header", "true")
      .option("inferSchema", "true")
      .csv("conf/sample/company_house_data.csv")
      .rdd

//    val headers: List[String] = chDF.schema.fieldNames.toList
//
//    val tmRdd = chDF.take(1)
//    println(tmRdd.mkString("[", ",\n ", "]"))

    //    tm.map { x => println(x); x }

    val rdd = sc.parallelize(Array(
      (toBytes("1"), (toBytes(columnFamily), toBytes("a"), toBytes("foo1"))),
      (toBytes("3"), (toBytes(columnFamily), toBytes("b"), toBytes("foo2.b")))
    ))

    def foo(r: Row) = {
      val ix = (0 until r.length).map(i => {
        println(s"head = ${r.schema(i).name} | value = ${r.get(i)} | id = ${i * 1000} | columnfamily = $columnFamily")
        1
      })
      ix
    }

    chDF.take(1).map(foo)

    //    rdd.hbaseBulkLoad(
    //      hbaseContext,
    //      TableName.valueOf(tableName),
    //      t => {
    //        val rowKey = t._1
    //        val family: Array[Byte] = t._2._1
    //        val qualifier = t._2._2
    //        val value: Array[Byte] = t._2._3
    //
    //        val keyFamilyQualifier: KeyFamilyQualifier = new KeyFamilyQualifier(rowKey, family, qualifier)
    //
    //        Seq((keyFamilyQualifier, value)).iterator
    //      },
    //      stagingFolder
    //    )
    println(headers.mkString(", "))
    //    chDF.printSchema()
    Ok(chDF.head().toString).as(JSON)
  }
}