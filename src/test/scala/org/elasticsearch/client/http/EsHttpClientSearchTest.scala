package org.elasticsearch.client.http

/**
  * Created by user on 6/15/17.
  */

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.elasticsearch.client.http.entities.{BasicAuthInfo, NoAuth}
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterAllConfigMap, ConfigMap, FunSuite}

import scala.io.Source

class EsHttpClientSearchTest extends FunSuite with BeforeAndAfterAll {

  var client: ESHttpClient = null
  private val index = "test"
  private val `type` = "doc"

  val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  override def beforeAll(): Unit = {
    val auth = (Option(System.getProperty("auth.user")), Option(System.getProperty("auth.pass"))) match {
      case (Some(user), Some(pass)) => BasicAuthInfo(user, pass)
      case _ => NoAuth()
    }
    client = new ESHttpClient(System.getProperty("servers", "localhost:9200").split(","), auth)
  }

  test("test doc with all types should success") {
    val docBody = Source.fromURL(getClass.getResource("/org/elasticsearch/search/query/all-example-document.json")).mkString
    val id = "1"
    val idxSetting =
      if (client.clusterInfo.version.number.split("\\.")(0).toInt < 5) {
        Source.fromURL(getClass.getResource("/org/elasticsearch/search/query/all-query-index-lt5.json")).mkString
      } else {
        Source.fromURL(getClass.getResource("/org/elasticsearch/search/query/all-query-index.json")).mkString
      }
    assert(client.createIndex(index, idxSetting).acknowledged)
    assert(client.index(index, `type`, Some(id), docBody).getId == "1")

    client.refresh(Set(index))
    //Wait 5 secs for index refresh
    println("Sleep 5 seconds for index refresh...")
    Thread.sleep(5000)

    var resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("foo")).toString)
    assert(resp.hits.total == 1L)
    resp = client.search("test", "doc", SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("Bar")).toString)
    assert(resp.hits.total == 1L)
    resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("Baz")).toString)
    assert(resp.hits.total == 1L)
    resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("19")).toString)
    assert(resp.hits.total == 1L)
    // nested doesn't match because it's hidden
    resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("1476383971")).toString)
    assert(resp.hits.total == 1L)
    // bool doesn't match
    resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("7")).toString)
    assert(resp.hits.total == 1L)
    resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("23")).toString)
    assert(resp.hits.total == 1L)
    resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("1293")).toString)
    assert(resp.hits.total == 1L)
    resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("42")).toString)
    assert(resp.hits.total == 1L)
    resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("1.7")).toString)
    assert(resp.hits.total == 1L)
    resp = client.search(index, `type`, SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("127.0.0.1")).toString)
    assert(resp.hits.total == 1L)
    // binary doesn't match
    // suggest doesn't match
    // geo_point doesn't match
    // geo_shape doesn't match
    assert(client.deleteIndies(index).acknowledged)
  }

  override def afterAll(): Unit = {
    super.afterAll()
    client.close()
  }

}
