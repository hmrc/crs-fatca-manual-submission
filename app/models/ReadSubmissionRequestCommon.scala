package models


import SubmissionsListResponseGenerator.{CRFA, RegimeType}
import play.api.libs.json.{Json, OFormat}

case class ReadSubmissionRequestCommon(
                           regime: RegimeType= CRFA,
                           originatingSystem: String = "MDTP",
                           transmittingSystem: String = "CADX",
                           requestParameters: Option[List[CommonParameters]]
                         )
object ReadSubmissionRequestCommon {
  implicit val format: OFormat[ReadSubmissionRequestCommon] = Json.format[ReadSubmissionRequestCommon]
}
