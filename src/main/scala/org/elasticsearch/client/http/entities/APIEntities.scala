package org.elasticsearch.client.http.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

//
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class IndexResponse(__index: String, __type: String, __id: String, __version: Long, created: Boolean) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version

  def isCreated: Boolean = created
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class UpdateResponse(__index: String, __type: String, __id: String, __version: Long) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class GetResponse(__index: String, __type: String, __id: String, __version: Long, found: Boolean, __source: Map[String, Any]) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version

  def getSource: Map[String, Any] = __source
}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class DeleteResponse(__index: String, __type: String, __id: String, __version: Long, found: Boolean) {
  def getIndex: String = __index

  def getType: String = __type

  def getId: String = __id

  def getVersion: Long = __version

  def isFound: Boolean = found
}

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