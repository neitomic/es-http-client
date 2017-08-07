package com.github.thanhtien522.eshttpclient.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

/**
  * @author Tien Nguyen
  */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[LowerCaseWithUnderscoresStrategy])
case class ClusterInfo(clusterName: String, version: VersionInfo)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(classOf[LowerCaseWithUnderscoresStrategy])
case class VersionInfo(number: String, luceneVersion: String)