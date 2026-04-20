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

package connectors

import config.AppConfig
import io.circe.literal.json
import org.scalatest.matchers.should.Matchers.should
import play.api.Application
import play.api.libs.json.Json
import utils.{SpecHelper, WireMockServerHandler}

import scala.language.postfixOps

class SubmissionConnectorISpec extends SpecHelper with WireMockServerHandler {

  lazy val app: Application = applicationBuilder()
    .configure(
      conf = "microservice.services.read-submission-history.port" -> server.port(),
      "auditing.enabled" -> "false"
    )
    .build()
  lazy val appConfig: AppConfig            = app.injector.instanceOf[AppConfig]
  lazy val connector: SubmissionsConnector = app.injector.instanceOf[SubmissionsConnector]
  lazy val url                        = s"/dac6/getlistofsubmissions/v1"

  val successResponse =
    json"""
  {
    "submissionsListResponse": {
      "responseCommon": {
        "regime": "CRFA",
        "responseParameters": [
          {
            "paramName": "paramName1",
            "paramValue": "paramValue1"
          }
        ]
      },
      "responseDetails": {
        "submissionsList": [
          {
            "fiId": "XZU9323406858",
            "fiName": "Test FI Name",
            "fileName": "GB2025GB-XZU9323406858-APIMB0256",
            "submissionStatus": "PASSED",
            "uploadDateTime": "2025-02-28T10:20:56.789Z",
            "regime": "CRS",
            "reportingYear": "2016",
            "submissionCaseId": "CRS-SUB-12224",
            "submissionType": "XML",
            "submissionFileType": "CRS702",
            "messageRefId": "GB2025GB-XZU9323406858-APIMB0256"
          },
          {
            "fiId": "XZU9323406868",
            "fiName": "Test FI Name1",
            "fileName": "GB2025GB-XZU9323406858-APIMB0266",
            "submissionStatus": "PASSED",
            "uploadDateTime": "2025-02-28T10:20:56.789Z",
            "regime": "FATCA",
            "reportingYear": "2016",
            "submissionCaseId": "FATCA-SUB-12224",
            "submissionType": "XML",
            "submissionFileType": "FATCA2",
            "messageRefId": "GB2025GB-XZU9323406858-APIMB0266",
            "submissionDeleteStatus": true
          }
        ]
      }
    }
  }
  """

  "SubmissionsConnector" - {
    "successfully parse response body into a list of submissions in case of a 200 response" in {
      readSubmissionRequestGen.map {
        requestPayload =>
          stubGet(url, 200, successResponse.toString)
          val result = connector.readSubmission(requestBody = requestPayload)
          result.futureValue mustEqual Json.parse(successResponse.toString)
      }

    }

  }

}
