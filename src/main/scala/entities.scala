import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
  * Created by user on 6/12/17.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class IndexResponse(_index: String, _type: String, _id: String, _version: Long, created: Boolean)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class UpdateResponse(_index: String, _type: String, _id: String, _version: Long)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class GetResponse(_index: String, _type: String, _id: String, _version: Long, found: Boolean, _source: Map[String, Any])

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class DeleteResponse(_index: String, _type: String, _id: String, _version: Long, found: Boolean)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class SearchResponse(timeOut: Boolean, took: Long, hits: SearchHits)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class SearchHits(total: Long, maxScore: Double, hits: Seq[SearchHit], aggregations: Map[String, Any])

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class SearchHit(_index: String, _type: String, _id: String, _score: Double, _source: Map[String, Any])

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class AckResponse(acknowledged: Boolean)