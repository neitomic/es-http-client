organization := "com.github.thanhtien522"

name := "es-http-client"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "org.elasticsearch.client" % "rest" % "5.4.1",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.8",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.8",
  "org.elasticsearch" % "elasticsearch" %"2.4.1" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

pomIncludeRepository := { _ => false }

licenses := Seq("MIT License" -> url("http://www.opensource.org/licenses/mit-license.html"))

publishArtifact in (Compile, packageBin) := true

publishArtifact in (Test, packageBin) := false

publishArtifact in (Compile, packageDoc) := false

publishArtifact in (Compile, packageSrc) := false

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