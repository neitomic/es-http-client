package org.elasticsearch.client.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.elasticsearch.client.http.entities.SearchResponse
import org.scalatest.FunSuite

/**
  * Created by 51103 on 019, 19, 6, 2017.
  */
class ApiEntitiesTest extends FunSuite {

  val searchResponseStr =
    """
      |{
      |  "took": 1,
      |  "timed_out": false,
      |  "_shards": {
      |    "total": 1,
      |    "successful": 1,
      |    "failed": 0
      |  },
      |  "hits": {
      |    "total": 1,
      |    "max_score": 0.3247595,
      |    "hits": [
      |      {
      |        "_score": 0.3247595,
      |        "_type": "doc",
      |        "_source": {
      |          "f_suggest": {
      |            "input": [
      |              "Nevermind",
      |              "Nirvana"
      |            ],
      |            "weight": 34
      |          },
      |          "f_multi": "Foo Bar Baz",
      |          "f_bool": "true",
      |          "f1": "foo",
      |          "f_long": "42",
      |          "f_date": "1476383971",
      |          "f3": "foo bar baz",
      |          "f_nested": {
      |            "nest1": "nfoo",
      |            "nest2": "nbar",
      |            "nest3": 21
      |          },
      |          "f_object": {
      |            "sub1": "sfoo",
      |            "sub2": "sbar",
      |            "sub3": 19
      |          },
      |          "f_binary": "VGhpcyBpcyBzb21lIGJpbmFyeSBkYXRhCg==",
      |          "f_short": "23",
      |          "f_byte": "7",
      |          "f_ip": "127.0.0.1",
      |          "f_geos": {
      |            "type": "point",
      |            "coordinates": [
      |              -77.03653,
      |              38.897676
      |            ]
      |          },
      |          "f_float": "1.7",
      |          "f_geop": "41.12,-71.34",
      |          "f2": "Bar",
      |          "f_int": "1293"
      |        },
      |        "_id": "1",
      |        "_index": "test"
      |      }
      |    ]
      |  }
      |}
    """.stripMargin

  val objectMapper = new ObjectMapper().registerModule(DefaultScalaModule)

  test("Deserialize SearchResponse from String should return success value") {

    val resp = objectMapper.readValue(searchResponseStr, classOf[SearchResponse])
    assert(resp.hits.total  == 1)
    assert(!resp.timeOut)
    assert(resp.hits.maxScore == 0.3247595)
  }
}
