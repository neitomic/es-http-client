package org.elasticsearch.client.http.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.{JsonDeserialize, JsonNaming}

trait DocRequest {

  /**
    * This function used for build bulk request body
    *
    * @return The ndjson format with the first line is meta data, the second line is document data
    */
  def toBulkJson(): String
}

case class DocIndexRequest(__index: Option[String], __type: Option[String], __id: Option[String], source: String) extends DocRequest {
  override def toBulkJson(): String = {
    val meta = {
      val tmp = Seq(
        __index.map(v => s""""_index" : "$v"""").getOrElse(""),
        __type.map(v => s""""_type" : "$v"""").getOrElse(""),
        __id.map(v => s""""_id" : "$v"""").getOrElse("")
      ).filter(_.nonEmpty).mkString(", ")
      s"""{ "index" : { $tmp } }"""
    }
    meta + "\n" + source.replaceAll("[\r\n]", "")
  }
}

case class DocDeleteRequest(__index: Option[String], __type: Option[String], __id: String) extends DocRequest {
  override def toBulkJson(): String = {
    val meta = {
      val tmp = Seq(
        __index.map(v => s""""_index" : "$v"""").getOrElse(""),
        __type.map(v => s""""_type" : "$v"""").getOrElse(""),
        s""""_index" : "${__id}""""
      ).filter(_.nonEmpty).mkString(", ")
      s"""{ "delete" : { $tmp } }"""
    }
    meta
  }
}

/**
  *
  * @param __index optional index of request, the index can be specified by API path
  * @param __type  optional index of request, the index can be specified by API path
  * @param __id    id of document
  * @param source  the document source
  */
case class DocUpdateRequest(__index: Option[String], __type: Option[String], __id: String, source: String) extends DocRequest {
  override def toBulkJson(): String = {
    val meta = {
      val tmp = Seq(
        __index.map(v => s""""_index" : "$v"""").getOrElse(""),
        __type.map(v => s""""_type" : "$v"""").getOrElse(""),
        s""""_index" : "${__id}""""
      ).filter(_.nonEmpty).mkString(", ")
      s"""{ "update" : { $tmp } }"""
    }
    meta + "\n" + s"""{ "doc": ${source.replaceAll("[\r\n]", "")}}"""
  }
}

case class SearchRequest(searchQuery: String, __index: Option[String] = None, __type: Option[String] = None, searchType: Option[String] = None) {

  def toMultiSearchNDJson: String = {
    val header = {
      val tmp = Seq(
        __index.map(v => s""""_index" : "$v"""").getOrElse(""),
        __type.map(v => s""""_type" : "$v"""").getOrElse(""),
        searchType.map(v => s""""search_type" : "$v"""").getOrElse("")
      ).filter(_.nonEmpty).mkString(", ")
      s"""{ $tmp }"""
    }
    header + "\n" + searchQuery.replaceAll("[\r\n]", "")
  }
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class GetRequest(__index: Option[String], __type: Option[String], __id: String)


trait DocResponse

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class IndexResponse(__index: String, __type: String, __id: String, __version: Long, created: Boolean) extends DocResponse {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version

  def isCreated: Boolean = created
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class UpdateResponse(__index: String, __type: String, __id: String, __version: Long) extends DocResponse {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class DeleteResponse(__index: String, __type: String, __id: String, __version: Long, found: Boolean) extends DocResponse {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version

  def isFound: Boolean = found
}

case class BulkResponse(took: Long, items: Seq[BulkItemResponse])

@JsonDeserialize(using = classOf[BulkItemResponseDeserializer])
case class BulkItemResponse(actionType: String, response: DocResponse)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class GetResponse(__index: String, __type: String, __id: String, __version: Long, found: Boolean, __source: Map[String, Any]) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version

  def getSource: Map[String, Any] = __source
}


case class MultiSearchResponse(responses: Seq[SearchResponse])

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class SearchResponse(timeOut: Boolean, took: Long, hits: SearchHits)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class SearchHits(total: Long, maxScore: Double, hits: Seq[SearchHit], aggregations: Map[String, Any])

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class SearchHit(__index: String, __type: String, __id: String, __score: Double, __source: Map[String, Any]) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getScore: Double = __score

  def getSource: Map[String, Any] = __source
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class AckResponse(acknowledged: Boolean)