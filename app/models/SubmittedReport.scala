package models

import SubmissionsListResponseGenerator.{RegimeType, SubmissionFileType, SubmissionStatus, SubmissionType}
import play.api.libs.json.{Json, OFormat}


case class SubmittedReport(
                            fiId: String,
                            fiName: String,
                            fileName: String,
                            submissionStatus: SubmissionStatus,
                            uploadDateTime: String,
                            regime: RegimeType,
                            reportingYear: String,
                            submissionCaseId: String,
                            submissionType: SubmissionType,
                            submissionFileType: SubmissionFileType,
                            messageRefId: String,
                            submissionDeleteStatus: Option[Boolean] = None,
                            originalMessageRefId: Option[String] = None,
                             )

object SubmittedReport {
  implicit val format : OFormat[SubmittedReport] = Json.format[SubmittedReport]
}

