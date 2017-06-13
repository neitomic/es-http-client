import org.apache.http.HttpHost
import org.elasticsearch.client.{Response, ResponseListener, RestClient, RestClientBuilder}
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.CredentialsProvider
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import java.io.InputStream
import java.nio.file.{Files, Paths}
import java.security.KeyStore
import java.util.Collections

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.twitter.util.{Future, Promise}
import org.apache.http.client.methods.{HttpDelete, HttpGet, HttpPost, HttpPut}
import org.apache.http.entity.ContentType
import org.apache.http.nio.entity.NStringEntity
import scala.collection.JavaConversions._

/**
  * Created by user on 6/8/17.
  */
class ESHttpClient(servers: Seq[String], authInfo: AuthInfo) {

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

      case BasicAuthWithEncryptAuthInfo(user, pass, keyStorePath, keyStorePass) =>
      //TODO: Implement encrypted communication
      case _ => //Do nothing
    }
    builder.build()
  }

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

  def close(): Unit = client.close()
}
