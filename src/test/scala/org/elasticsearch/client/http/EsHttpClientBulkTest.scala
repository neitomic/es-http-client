package org.elasticsearch.client.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.elasticsearch.client.http.entities._
import org.scalatest.{BeforeAndAfterAll, FunSuite}

class EsHttpClientBulkTest extends FunSuite with BeforeAndAfterAll {

  var client: ESHttpClient = null

  val index = "twitter_bulk"

  val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  override def beforeAll(): Unit = {
    val auth = (Option(System.getProperty("auth.user")), Option(System.getProperty("auth.pass"))) match {
      case (Some(user), Some(pass)) => BasicAuthInfo(user, pass)
      case _ => NoAuth()
    }
    client = new ESHttpClient(System.getProperty("servers", "localhost:9200").split(","), auth)
    assert(client.createIndex(index, "{}").acknowledged)
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

  val updatedDoc = Map(
    "user" -> "kimchy",
    "post_date" -> "2009-11-15T14:12:14",
    "message" -> "trying out ElasticSearch"
  )

  test("test bulk") {
    val requests = Seq(
      DocIndexRequest(None, None, Some("1"), objectMapper.writeValueAsString(doc)),
      DocUpdateRequest(None, None, "1", objectMapper.writeValueAsString(updateDoc))
    )
    val resp = client.bulk(Some(index), Some("tweet"), requests)
    assert(resp.items.length == requests.length)
    assert(resp.items.head.actionType =="index")
    assert(resp.items.head.response.isInstanceOf[IndexResponse])
    val indexResp = resp.items.head.response.asInstanceOf[IndexResponse]
    assert(indexResp.getIndex == index)
    assert(indexResp.getType == "tweet")
    assert(indexResp.getId == "1")
    //Created does not work with index
    //assert(indexResp.created)
    assert(indexResp.getVersion == 1)

    assert(resp.items.last.actionType =="update")
    assert(resp.items.last.response.isInstanceOf[UpdateResponse])
    val updateResp = resp.items.last.response.asInstanceOf[UpdateResponse]
    assert(updateResp.getIndex == index)
    assert(updateResp.getType == "tweet")
    assert(updateResp.getId == "1")
    assert(updateResp.getVersion == 2)


    val getResp = client.get(index, "tweet", "1")
    assert(getResp.getIndex == index)
    assert(getResp.getType == "tweet")
    assert(getResp.getId == "1")
    assert(getResp.found)
    assert(getResp.getVersion == 2)
    assert(getResp.getSource == updatedDoc)

  }

  override def afterAll(): Unit = {
    client.deleteIndies(index)
    client.close()
  }

}

