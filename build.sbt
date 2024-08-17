ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "scala-akka-mini",
    idePackagePrefix := Some("com.camilo")
  )


resolvers += "Akka library repository".at("https://repo.akka.io/maven")


lazy val AkkaVersion = "2.9.4"
lazy val AkkaHttpVersion = "10.6.3"
lazy val circeVersion    = "0.14.7"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,

  "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "1.2.1",
  "com.typesafe.akka" %% "akka-persistence" % AkkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % AkkaVersion,

  "com.typesafe.akka" %% "akka-cluster-tools" % AkkaVersion,

  "io.circe"          %% "circe-core"                 % circeVersion,
  "io.circe"          %% "circe-generic"              % circeVersion,
  "io.circe"          %% "circe-parser"               % circeVersion,

//  "com.typesafe.akka" %% "akka-http-testkit"          % AkkaVersion % Test,
//  "com.typesafe.akka" %% "akka-actor-testkit-typed"   % AkkaVersion     % Test,
  "org.scalatest"     %% "scalatest"                  % "3.2.9"         % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % AkkaVersion % Test,
)
