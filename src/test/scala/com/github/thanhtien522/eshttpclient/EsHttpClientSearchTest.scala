package com.github.thanhtien522.eshttpclient

/**
  * Created by user on 6/15/17.
  */

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.github.thanhtien522.eshttpclient.entities.SearchRequest
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

    val docBody = Source.fromURL(getClass.getResource("/org/elasticsearch/search/query/all-example-document.json")).mkString
    val id = "1"
    val idxSetting = Source.fromURL(getClass.getResource(s"/org/elasticsearch/search/query/all-query-index-v${client.clusterInfo.version.number.split("\\.")(0)}.json")).mkString

    assert(client.createIndex(index, idxSetting).acknowledged)
    assert(client.index(index, `type`, Some(id), docBody).getId == "1")
    client.refresh(Set(index))
    Thread.sleep(2000)
  }

  test("test doc with all types should success") {

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

  }

  test("test msearch") {
    val request = Seq(
      SearchRequest(SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("foo")).toString),
      SearchRequest(SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("Bar")).toString),
      SearchRequest(SearchSourceBuilder.searchSource().query(QueryBuilders.queryStringQuery("1476383971")).toString)
    )
    val resp = client.msearch(Set(index), Set.empty, request)

    assert(resp.responses.length == request.length)
    for (elem <- resp.responses) {
      assert(elem.hits.total == 1L)
    }
  }

  override def afterAll(): Unit = {
    super.afterAll()
    assert(client.deleteIndies(index).acknowledged)
    client.close()
  }

}
