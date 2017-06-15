import java.util.Collections

import org.elasticsearch.client.{Response, ResponseListener}

/**
  * Created by user on 6/8/17.
  */
object ESHttpClientTest {
  val client = new ESHttpClient(Seq("http://169.56.144.40:9200"), BasicAuthInfo("root", "123123"))

  def main(args: Array[String]): Unit = {
//    val resp = client.getClient.performRequest("GET", "/", Collections.singletonMap("pretty", "true"))
//
//    val byte = new Array[Byte](resp.getEntity.getContentLength.asInstanceOf[Int])
//    resp.getEntity.getContent.read(byte)
//    println(new String(byte))
//
//    client.getClient.performRequestAsync("GET", "/", Collections.singletonMap("pretty", "true"),
//      new ResponseListener {
//        override def onFailure(exception: Exception): Unit = ???
//        override def onSuccess(response: Response): Unit = ???
//      }
//    )

//    val resp = client.createIndex("circe_dev", "{}")
    val resp = client.getClient.performRequest("DELETE", "circe_dev")
    resp

    client.getClient.close()
  }




}
