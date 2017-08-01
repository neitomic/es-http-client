package com.github.thanhtien522.eshttpclient

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.commons.logging.LogFactory
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.methods._
import org.apache.http.entity.ContentType
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.apache.http.nio.entity.{NByteArrayEntity, NStringEntity}
import org.apache.http.{Consts, HttpHost}
import com.github.thanhtien522.eshttpclient.entities._
import org.elasticsearch.client.{RestClient, RestClientBuilder}

import scala.collection.JavaConversions._

/**
  * Created by user on 6/8/17.
  */
class ESHttpClient(servers: Seq[String], authInfo: AuthInfo) {

  private val logger = LogFactory.getLog(classOf[ESHttpClient])

  private final val APPLICATION_X_NDJSON = ContentType.create("application/x-ndjson", Consts.UTF_8)

  private val objectMapper: ObjectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  private val client = {
    val builder = RestClient.builder(servers.map(HttpHost.create): _*)
    authInfo match {
      case NoAuth() => //Do nothing
      case BasicAuthInfo(user, pass) =>
        val credentialsProvider = new BasicCredentialsProvider
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pass))
        builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
          override def customizeHttpClient(httpClientBuilder: HttpAsyncClientBuilder): HttpAsyncClientBuilder =
            httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
        })

      case BasicAuthWithEncryptAuthInfo(_, _, _, _) =>
        throw new IllegalArgumentException("BasicAuthWithEncryptAuthInfo currently does not supported.")
      case _ => //Do nothing
    }
    builder.build()
  }

  val clusterInfo: ClusterInfo = {
    val resp = client.performRequest(HttpGet.METHOD_NAME, "/", Map.empty[String, String])
    objectMapper.readValue(resp.getEntity.getContent, classOf[ClusterInfo])
  }

  logger.info("===============================================")
  logger.info("Cluster basic information:")
  logger.info(s"Cluster name:     ${clusterInfo.clusterName}")
  logger.info(s"ES version:       ${clusterInfo.version.number}")
  logger.info(s"Lucene version:   ${clusterInfo.version.luceneVersion}")
  logger.info("===============================================")

  def getClient: RestClient = client

  def index(index: String, `type`: String, id: Option[String], source: String): IndexResponse = {
    val resp = client.performRequest(HttpPost.METHOD_NAME,
      s"/$index/${`type`}${id.map(i => s"/$i").getOrElse("")}",
      Map.empty[String, String],
      new NStringEntity(source, ContentType.APPLICATION_JSON))
    objectMapper.readValue(resp.getEntity.getContent, classOf[IndexResponse])
  }

  def update(index: String, `type`: String, id: String, source: String, docAsUpsert: Boolean = false): UpdateResponse = {
    val updateSource =
      s"""{
         | "doc": $source,
         | "doc_as_upsert": $docAsUpsert
         |}""".stripMargin

    val resp = client.performRequest(HttpPost.METHOD_NAME,
      s"/$index/${`type`}/$id/_update",
      Map.empty[String, String],
      new NStringEntity(updateSource, ContentType.APPLICATION_JSON))
    objectMapper.readValue(resp.getEntity.getContent, classOf[UpdateResponse])
  }

  def get(index: String, `type`: String, id: String): GetResponse = {
    val resp = client.performRequest(HttpGet.METHOD_NAME,
      s"/$index/${`type`}/$id")
    objectMapper.readValue(resp.getEntity.getContent, classOf[GetResponse])
  }

  def delete(index: String, `type`: String, id: String): DeleteResponse = {
    val resp = client.performRequest(HttpDelete.METHOD_NAME,
      s"/$index/${`type`}/$id")
    objectMapper.readValue(resp.getEntity.getContent, classOf[DeleteResponse])
  }

  def bulk(index: Option[String], `type`: Option[String], requests: Seq[DocRequest]): BulkResponse = {
    val bodyString = requests.map(_.toBulkJson()).mkString("\n") + "\n"
    val resp = client.performRequest(HttpPost.METHOD_NAME,
      "/" + index.map(_ + "/").getOrElse("") + `type`.map(_ + "/").getOrElse("") + "_bulk",
      Map.empty[String, String],
      new NByteArrayEntity(bodyString.getBytes(Consts.UTF_8), APPLICATION_X_NDJSON)
    )
    objectMapper.readValue(resp.getEntity.getContent, classOf[BulkResponse])
  }

  /**
    * Search
    *
    * @param indies Set of index name
    * @param types  Set of type name
    * @param query  ElasticSearch json query
    * @return
    */
  def search(indies: Set[String], types: Set[String], query: String): SearchResponse = {
    val endpoint = (if (indies.isEmpty) "" else indies.mkString(",") + "/") + (if (types.isEmpty) "" else types.mkString(",") + "/") + "_search"
    val resp = client.performRequest(HttpPost.METHOD_NAME,
      endpoint,
      Map.empty[String, String],
      new NStringEntity(query, ContentType.APPLICATION_JSON))
    objectMapper.readValue(resp.getEntity.getContent, classOf[SearchResponse])
  }

  def msearch(indies: Set[String], types: Set[String], requests: Seq[SearchRequest]): MultiSearchResponse = {
    val body = requests.map(_.toMultiSearchNDJson).mkString("\n") + "\n"
    val resp = client.performRequest(HttpPost.METHOD_NAME,
      (if (indies.isEmpty) "" else indies.mkString(",") + "/") + (if (types.isEmpty) "" else types.mkString(",") + "/") + "_msearch",
      Map.empty[String, String],
      new NByteArrayEntity(body.getBytes(Consts.UTF_8), APPLICATION_X_NDJSON)
    )
    objectMapper.readValue(resp.getEntity.getContent, classOf[MultiSearchResponse])
  }

  def search(index: String, `type`: String, query: String): SearchResponse =
    search(Set(index), Set(`type`), query)

  def search(index: String, types: Set[String], query: String): SearchResponse =
    search(Set(index), types, query)

  def createIndex(indexName: String, settingsAndMappings: String): AckResponse = {
    val resp = client.performRequest(HttpPut.METHOD_NAME, s"/$indexName",
      Map.empty[String, String],
      new NStringEntity(settingsAndMappings, ContentType.APPLICATION_JSON)
    )
    objectMapper.readValue(resp.getEntity.getContent, classOf[AckResponse])
  }

  /**
    * Delete one or multiple index
    *
    * @param indies single index, multiple index with comma separated,
    *               all indies with _all, wildcard expression
    * @return
    */
  def deleteIndies(indies: String): AckResponse = {
    val resp = client.performRequest(HttpDelete.METHOD_NAME, s"/$indies")
    objectMapper.readValue(resp.getEntity.getContent, classOf[AckResponse])
  }

  def indexExist(index: String): Boolean = {
    val resp = client.performRequest(HttpHead.METHOD_NAME, s"$index")
    resp.getStatusLine.getStatusCode match {
      case 200 => true
      case 404 => false
      case _ =>
        //TODO: Should we handle others code?
        throw new Exception("Invalid http response code")
    }
  }

  def refresh(indies: Set[String]): Unit = {
    val resp = client.performRequest(HttpPost.METHOD_NAME, s"/${indies.mkString(",")}/_refresh")
    resp.getStatusLine.getStatusCode match {
      case 200 =>
      case code => throw new Exception(s"Refresh topic return code $code")
    }
  }

  def close(): Unit = client.close()
}
