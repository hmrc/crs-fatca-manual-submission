package models

import play.api.libs.json.{Json, OFormat}

case class ReadSubmissionResponse(submissionsListResponse: SubmissionsListResponse)

object ReadSubmissionResponse {
  implicit val format: OFormat[ReadSubmissionResponse] = Json.format[ReadSubmissionResponse]
}
