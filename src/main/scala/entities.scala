import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
  * Created by user on 6/12/17.
  */
@JsonIgnoreProperties(ignoreUnknown = true)
case class IndexResponse(_index: String, _type: String, _id: String, _version: Long, created: Boolean)

@JsonIgnoreProperties(ignoreUnknown = true)
case class UpdateResponse(_index: String, _type: String, _id: String, _version: Long)

@JsonIgnoreProperties(ignoreUnknown = true)
case class GetResponse(_index: String, _type: String, _id: String, _version: Long, found: Boolean, _source: Map[String, Any])

@JsonIgnoreProperties(ignoreUnknown = true)
case class DeleteResponse(_index: String, _type: String, _id: String, _version: Long, found: Boolean)