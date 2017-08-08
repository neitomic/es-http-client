package com.github.thanhtien522.eshttpclient.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

trait DocRequest {

  /**
    * This function used for build bulk request body
    *
    * @return The ndjson format with the first line is meta data, the second line is document data
    */
  def toBulkJson(): String
}

/**
  * Index document request
  *
  * @param __index optional index of request, the index can be specified by API path
  * @param __type  optional type of request, the type can be specified by API path
  * @param __id    id of document
  * @param source  the document source
  */
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

/**
  * Index document request
  *
  * @param __index optional index of request, the index can be specified by API path
  * @param __type  optional type of request, the type can be specified by API path
  * @param __id    id of document
  */
case class DocDeleteRequest(__index: Option[String], __type: Option[String], __id: String) extends DocRequest {
  override def toBulkJson(): String = {
    val meta = {
      val tmp = Seq(
        __index.map(v => s""""_index" : "$v"""").getOrElse(""),
        __type.map(v => s""""_type" : "$v"""").getOrElse(""),
        s""""_id" : "${__id}""""
      ).filter(_.nonEmpty).mkString(", ")
      s"""{ "delete" : { $tmp } }"""
    }
    meta
  }
}

/**
  * Update document request
  *
  * @param __index optional index of request, the index can be specified by API path
  * @param __type  optional type of request, the type can be specified by API path
  * @param __id    id of document
  * @param source  the document source
  */
case class DocUpdateRequest(__index: Option[String], __type: Option[String], __id: String, source: String) extends DocRequest {
  override def toBulkJson(): String = {
    val meta = {
      val tmp = Seq(
        __index.map(v => s""""_index" : "$v"""").getOrElse(""),
        __type.map(v => s""""_type" : "$v"""").getOrElse(""),
        s""""_id" : "${__id}""""
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
case class GetRequest(__index: Option[String], __type: Option[String], __id: String)

@JsonDeserialize(using = classOf[ErrorDeserializer])
case class Error(`type`: String, reason: String, causedBy: Error)

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class BaseResponse(val status: Int, val error: Error)

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class AbstractDocResponse(val __index: String,
                                   val __type: String,
                                   val __id: String,
                                   val __version: Long,
                                   override val status: Int,
                                   override val error: Error
                                  ) extends BaseResponse(status, error)

@JsonIgnoreProperties(ignoreUnknown = true)
case class IndexResponse(override val __index: String,
                         override val __type: String,
                         override val __id: String,
                         override val __version: Long,
                         created: Boolean,
                         override val status: Int,
                         override val error: Error
                        ) extends AbstractDocResponse(__index, __type, __id, __version, status, error) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version

  def isCreated: Boolean = created
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class UpdateResponse(override val __index: String,
                          override val __type: String,
                          override val __id: String,
                          override val __version: Long,
                          override val status: Int,
                          override val error: Error
                         ) extends AbstractDocResponse(__index, __type, __id, __version, status, error) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class DeleteResponse(override val __index: String,
                          override val __type: String,
                          override val __id: String,
                          override val __version: Long,
                          found: Boolean,
                          override val status: Int,
                          override val error: Error
                         ) extends AbstractDocResponse(__index, __type, __id, __version, status, error) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version

  def isFound: Boolean = found
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class BulkResponse(took: Long, errors: Boolean, items: Seq[BulkItemResponse])

@JsonDeserialize(using = classOf[BulkItemResponseDeserializer])
case class BulkItemResponse(actionType: String, response: AbstractDocResponse)

@JsonIgnoreProperties(ignoreUnknown = true)
case class GetResponse(__index: String, __type: String, __id: String, __version: Long, found: Boolean, __source: Map[String, Any]) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version

  def getSource: Map[String, Any] = __source
}


case class MultiSearchResponse(responses: Seq[SearchResponse])

@JsonIgnoreProperties (ignoreUnknown = true)
case class SearchResponse(timeOut: Boolean, took: Long, hits: SearchHits, aggregations: Map[String, Any])

@JsonIgnoreProperties(ignoreUnknown = true)
case class SearchHits(total: Long, maxScore: Double, hits: Seq[SearchHit])

@JsonIgnoreProperties(ignoreUnknown = true)
case class SearchHit(__index: String, __type: String, __id: String, __score: Double, __source: Map[String, Any]) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getScore: Double = __score

  def getSource: Map[String, Any] = __source
}

@JsonIgnoreProperties(ignoreUnknown = true)
case class AckResponse(acknowledged: Boolean)