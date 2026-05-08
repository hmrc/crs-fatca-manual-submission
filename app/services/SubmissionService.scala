/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import com.google.inject.Inject
import connectors.SubmissionsConnector
import models.{
  ReadSubmissionRequest,
  ReadSubmissionRequestCommon,
  ReadSubmissionRequestDetails,
  ReadSubmissionResponseDetails,
  RequestSubmissionHistoryParameters,
  SubmissionsListRequest,
  UserData
}
import org.apache.pekko.Done
import play.api.libs.json.{JsObject, Json}
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import repositories.SubmissionsRepository
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class SubmissionService @Inject() (submissionsConnector: SubmissionsConnector, repository: SubmissionsRepository)(implicit ec: ExecutionContext) {

  def readAndMaybeCache(requestBody: RequestSubmissionHistoryParameters, subscriptionId: String)(implicit
    hc: HeaderCarrier
  ): Future[ReadSubmissionResponseDetails] = {
    val submissionRequest = ReadSubmissionRequest(
      SubmissionsListRequest(requestDetails = ReadSubmissionRequestDetails(subscriptionId, requestBody.fiId), requestCommon = ReadSubmissionRequestCommon())
    )
    for
      submissionResponse <- submissionsConnector.readSubmission(submissionRequest)
      submissions = submissionResponse.submissionsListResponse.responseDetails
      // NOTE: we should wipe their data and replace with new record if cache option is true
      _ <- if (requestBody.shouldCache) repository.set(UserData(subscriptionId, Json.toJson(submissions).as[JsObject])) else Future.successful(Done)
    yield submissions
  }
}
