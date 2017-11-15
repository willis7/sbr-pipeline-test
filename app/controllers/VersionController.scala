package controllers

import javax.inject.Singleton

import io.swagger.annotations.{ Api, ApiOperation, ApiResponse, ApiResponses }
import play.api.mvc.{ Action, Controller }
//import org.apache.spark.rdd
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

    Ok("").as(JSON)
  }
}