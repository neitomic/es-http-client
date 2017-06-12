import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.scalatest._

/**
  * Created by user on 6/12/17.
  */
class ESHttpClientUTest extends FunSuite with BeforeAndAfterAllConfigMap  {

  var client: ESHttpClient = null

  val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  override def beforeAll(configMap: ConfigMap): Unit = {
    val auth = (configMap.getOptional[String]("user"), configMap.getOptional[String]("pass")) match {
      case (Some(user), Some(pass)) => BasicAuthInfo(user, pass)
      case _ => NoAuth()
    }
    client = new ESHttpClient(configMap.getWithDefault("servers", Seq("localhost:9200")),auth)
  }

  val doc = Map(
    "user" -> "kimchy",
    "post_date" -> "2009-11-15T14:12:12",
    "message" -> "trying out Elasticsearch"
  )

  val updateDoc = Map(
    "post_date" -> "2009-11-15T14:12:14",
    "message" -> "trying out ElasticSearch"
  )

  val updatedDoc =  Map(
    "user" -> "kimchy",
    "post_date" -> "2009-11-15T14:12:14",
    "message" -> "trying out ElasticSearch"
  )

  test("Index with id should return correct response") {
    val resp = client.index("twitter", "tweet", Some("1"), objectMapper.writeValueAsString(doc))
    assert(resp._index == "twitter")
    assert(resp._type == "tweet")
    assert(resp._id == "1")
    assert(resp.created)
    assert(resp._version == 1)
  }

  test("Get on first doc should return correct source") {
    val resp = client.get("twitter", "tweet", "1")
    assert(resp._index == "twitter")
    assert(resp._type == "tweet")
    assert(resp._id == "1")
    assert(resp.found)
    assert(resp._version == 1)
    assert(resp._source == doc)
  }

  test("Update should return correct response") {
    val resp = client.update("twitter", "tweet", "1", objectMapper.writeValueAsString(updateDoc))
    assert(resp._index == "twitter")
    assert(resp._type == "tweet")
    assert(resp._id == "1")
    assert(resp._version == 2)
  }

  test("Get on updated doc should return correct source") {
    val resp = client.get("twitter", "tweet", "1")
    assert(resp._index == "twitter")
    assert(resp._type == "tweet")
    assert(resp._id == "1")
    assert(resp.found)
    assert(resp._version == 2)
    assert(resp._source == updatedDoc)
  }

  test("Delete should return correct response") {
    val resp = client.delete("twitter", "tweet", "1")
    assert(resp._index == "twitter")
    assert(resp._type == "tweet")
    assert(resp._id == "1")
    assert(resp.found)
    assert(resp._version == 2)
  }

  override def afterAll(configMap: ConfigMap): Unit = {
    client.close()
  }

}
