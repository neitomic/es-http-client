package com.github.thanhtien522.eshttpclient

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.thanhtien522.eshttpclient.entities.{BasicAuthInfo, NoAuth}
import org.scalatest._

/**
  * Created by user on 6/12/17.
  */
class ESHttpClientCRUDTest extends FunSuite with BeforeAndAfterAll {

  var client: ESHttpClient = null

  val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  override def beforeAll(): Unit = {
    val auth = (Option(System.getProperty("auth.user")), Option(System.getProperty("auth.pass"))) match {
      case (Some(user), Some(pass)) => BasicAuthInfo(user, pass)
      case _ => NoAuth()
    }
    client = new ESHttpClient(System.getProperty("servers", "localhost:9200").split(","), auth)
    assert(client.createIndex("twitter", "{}").acknowledged)
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

  test("Index with id should return correct response") {
    val resp = client.index("twitter", "tweet", Some("1"), objectMapper.writeValueAsString(doc))
    assert(resp.getIndex == "twitter")
    assert(resp.getType == "tweet")
    assert(resp.getId == "1")
    assert(resp.created)
    assert(resp.getVersion == 1)
  }

  test("Get on first doc should return correct source") {
    val resp = client.get("twitter", "tweet", "1")
    assert(resp.getIndex == "twitter")
    assert(resp.getType == "tweet")
    assert(resp.getId == "1")
    assert(resp.found)
    assert(resp.getVersion == 1)
    assert(resp.getSource == doc)
  }

  test("Update should return correct response") {
    val resp = client.update("twitter", "tweet", "1", objectMapper.writeValueAsString(updateDoc))
    assert(resp.getIndex == "twitter")
    assert(resp.getType == "tweet")
    assert(resp.getId == "1")
    assert(resp.getVersion == 2)
  }

  test("Get on updated doc should return correct source") {
    val resp = client.get("twitter", "tweet", "1")
    assert(resp.getIndex == "twitter")
    assert(resp.getType == "tweet")
    assert(resp.getId == "1")
    assert(resp.found)
    assert(resp.getVersion == 2)
    assert(resp.getSource == updatedDoc)
  }

  test("Delete should return correct response") {
    val resp = client.delete("twitter", "tweet", "1")
    assert(resp.getIndex == "twitter")
    assert(resp.getType == "tweet")
    assert(resp.getId == "1")
    assert(resp.found)
    assert(resp.getVersion == 3)
  }

  override def afterAll(): Unit = {
    client.deleteIndies("twitter")
    client.close()
  }

}
