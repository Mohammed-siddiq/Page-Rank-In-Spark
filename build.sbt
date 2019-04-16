name := "Spark-PageRank"
scalaVersion := "2.11.11"
lazy val commonSettings = Seq(
  organization := "MohammedSiddiq.HW5",
  version := "0.1.0"
)
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
lazy val app = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "PageRankSpark"
  ).
  enablePlugins(AssemblyPlugin)
libraryDependencies ++= Seq("org.slf4j" % "slf4j-api" % "1.7.5",
  "org.slf4j" % "slf4j-simple" % "1.7.5", "com.typesafe" % "config" % "1.3.2", "com.databricks" %% "spark-xml" % "0.5.0",
  "org.apache.spark" %% "spark-core" % "2.4.0", "org.apache.spark" %% "spark-sql" % "2.4.0")

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}