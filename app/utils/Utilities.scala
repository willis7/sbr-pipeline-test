package utils

import java.io.File

import play.api.libs.json._

/**
 * Created by haqa on 05/07/2017.
 */
object Utilities {

  def currentDirectory = new File(".").getCanonicalPath

  def errAsJson(status: Int, code: String, msg: String): JsObject = {
    Json.obj(
      "status" -> status,
      "code" -> code,
      "message_en" -> msg
    )
  }

  def getElement(value: Any) = {
    val res = value match {
      case None => ""
      case Some(i: Int) => i
      case Some(l: Long) => l
      case Some(z) => s""""${z}""""
      case x => s"${x.toString}"
    }
    res
  }

}