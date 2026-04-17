package models


import SubmissionsListResponseGenerator.RegimeType
import play.api.libs.json.{Json, OFormat}

case class ReadSubmissionResponseCommon(
                           regime: RegimeType,
                           responseParameters: List[CommonParameters] = List.empty
                         )
object ReadSubmissionResponseCommon {
  implicit val format: OFormat[ReadSubmissionResponseCommon] = Json.format[ReadSubmissionResponseCommon]
}
