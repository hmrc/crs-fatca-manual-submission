package models

import play.api.libs.json.{Json, OFormat}

case class ReadSubmissionResponseDetails(
  submissionsList: List[SubmittedReport]
)

object ReadSubmissionResponseDetails {
  implicit val format: OFormat[ReadSubmissionResponseDetails] = Json.format[ReadSubmissionResponseDetails]
}
