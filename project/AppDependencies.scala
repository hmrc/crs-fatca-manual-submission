import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  private val bootstrapVersion = "10.7.0"
  private val hmrcMongoVersion = "2.12.0"
  private val circeVersion     = "0.14.15"

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"        % hmrcMongoVersion,
    "io.circe"          %% "circe-core"                % circeVersion,
    "io.circe"          %% "circe-generic"             % circeVersion,
    "io.circe"          %% "circe-parser"              % circeVersion,
    "io.circe"          %% "circe-literal"             % circeVersion
  )

  val test = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"  % bootstrapVersion % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-30" % hmrcMongoVersion % Test,
    "org.scalacheck"    %% "scalacheck"                % "1.19.0"       % Test
  )

  val it = Seq.empty
}
