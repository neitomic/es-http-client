import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.thanhtien522.eshttpclient.entities.{BulkResponse, SearchResponse}

val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

val str = """{"took":3,"timed_out":false,"_shards":{"total":5,"successful":5,"failed":0},"hits":{"total":0,"max_score":0.0,"hits":[]},"aggregations":{"count_all_events.type":{"value":0}}}"""

objectMapper.readValue(str, classOf[SearchResponse])


val hits =
  """
    |{"total":0,"max_score":0.0,"hits":[]},"aggregations":{"count_all_events.type":{"value":0}}
  """.stripMargin