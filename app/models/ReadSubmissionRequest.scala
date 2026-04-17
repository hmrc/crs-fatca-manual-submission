package models

import play.api.libs.json.{Json, OFormat}

case class ReadSubmissionRequest(submissionsListRequest: SubmissionsListRequest)

object ReadSubmissionRequest {
  implicit val format: OFormat[ReadSubmissionRequest] = Json.format[ReadSubmissionRequest]
}
