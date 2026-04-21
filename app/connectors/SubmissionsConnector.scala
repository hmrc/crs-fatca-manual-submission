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

import com.google.inject.Inject
import config.AppConfig
import connectors.HeaderGenerator.defaultHeaders
import models.HodErrors.{InvalidJson, UnexpectedResponse}
import models.{ReadSubmissionRequest, ReadSubmissionResponse}
import play.api.Logging
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.libs.ws.JsonBodyWritables.*
import repositories.SubmissionsRepository
import uk.gov.hmrc.http.HttpReads.Implicits.readRaw
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse, StringContextOps}

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}



class SubmissionsConnector @Inject()(val config: AppConfig,
                                     val repository: SubmissionsRepository,
                                     val http: HttpClientV2)(implicit ec:ExecutionContext) extends Logging{
  
  
  def readSubmission(requestBody: ReadSubmissionRequest)(implicit hc:HeaderCarrier): Future[ReadSubmissionResponse] ={
    val correlationID=  UUID.randomUUID()
    http
      .post(url"${config.readSubmissionUrl}/dac6/getlistofsubmission")
      .withBody(Json.toJson(requestBody))
      .setHeader(defaultHeaders(config.readSubmissionToken, correlationID): _*)
      .execute[HttpResponse].flatMap{
        case res if res.status == 200 => res.json.validate[ReadSubmissionResponse] match {
          case JsSuccess(submissionHistory, _) => Future.successful(submissionHistory)
          case JsError(errors) =>
            logger.warn(s"Invalid json returned; $errors for corr ID : ${correlationID.toString}")
            Future.failed(InvalidJson)
        }
        case res =>
          logger.error(s"Unsuccessful call made to retrieve submission data for ${correlationID.toString}")
          Future.failed(UnexpectedResponse)
      }
  }

}
