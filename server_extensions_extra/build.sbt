val catsVersion = "1.4.0"
val catsEffectVersion = "1.0.0"
val circeVersion = "0.10.0-M2"
val http4sVersion = "0.19.0-SNAPSHOT"
val xchangeVersion = "4.3.11-SNAPSHOT"
val catsRetryVersion = "0.1.0"
val monocleVersion = "1.5.0-cats"

resolvers ++= Seq(
    "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    "JCenter" at "https://jcenter.bintray.com/"
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")

val dependencies = Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-core" % http4sVersion,
  "org.http4s" %% "http4s-client" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,
  "org.http4s" %% "http4s-circe" % http4sVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.github.cb372" %% "cats-retry-core"        % catsRetryVersion,
  "com.github.cb372" %% "cats-retry-cats-effect" % catsRetryVersion,
  "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,

  "javax.ws.rs" % "javax.ws.rs-api" % "2.1" jar() withSources() withJavadoc(),

  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.knowm.xchange" % "xchange-core" % xchangeVersion,
  "org.knowm.xchange" % "xchange-bittrex" % xchangeVersion,

  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

lazy val root = (project in file("."))
  .settings(
    name := "batm_server_extensions_decent",
    scalaVersion := "2.12.6",
    scalacOptions ++= Seq("-Ypartial-unification", "-unchecked", "-language:higherKinds", "-language:postfixOps"),
    libraryDependencies ++= dependencies,
    updateOptions := updateOptions.value.withLatestSnapshots(false)
  )

val meta = """META.INF(.)*""".r
assemblyMergeStrategy in assembly := {
  case meta(_) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

test in assembly := {}


