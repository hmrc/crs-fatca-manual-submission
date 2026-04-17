package models

import play.api.libs.json.{Json, OFormat}

case class ReadSubmissionRequestDetails(subscriptionId: String, fiId: Option[String])

object ReadSubmissionRequestDetails {
  implicit val format: OFormat[ReadSubmissionRequestDetails] = Json.format[ReadSubmissionRequestDetails]
}
