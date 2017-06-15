name := "es_http_client"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.11"

resolvers += "twitter" at "https://maven.twttr.com"

libraryDependencies ++= Seq(
  "com.twitter" %% "util-core" % "6.43.0",
  "org.elasticsearch.client" % "rest" % "5.4.1",
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % "2.8.6",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.6",
  "com.github.wnameless" % "json-flattener" % "0.4.0",
  "org.elasticsearch" % "elasticsearch" %"2.4.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

