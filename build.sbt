organization := "com.github.thanhtien522"

name := "es-http-client"

version := "0.2.1"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "org.elasticsearch.client" % "rest" % "5.4.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.5",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.6.5",
  "org.elasticsearch" % "elasticsearch" %"2.4.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

pomIncludeRepository := { _ => false }

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html"))

publishArtifact in (Test, packageBin) := false

publishMavenStyle := true

homepage := Some(url("https://github.com/thanhtien522/es-http-client"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/thanhtien522/es-http-client"),
    "scm:git@github.com:thanhtien522/es-http-client.git"
  )
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}


developers := List(
  Developer(id="thanhtien522", name="Tien Nguyen", email="thanhtien522@gmail.com", url=url("https://github.com/thanhtien522"))
)
