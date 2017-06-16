package org.elasticsearch.client.http.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
  * Created by user on 6/15/17.
  */
class ClusterEntities {

}

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class ClusterInfo(clusterName: String, version: VersionInfo)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[SnakeCaseStrategy])
case class VersionInfo(number: String, luceneVersion: String)