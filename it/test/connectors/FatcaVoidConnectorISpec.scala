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
import org.scalatest.RecoverMethods.recoverToExceptionIf
import org.scalatest.matchers.should.Matchers.should
import play.api.Application
import uk.gov.hmrc.http.UpstreamErrorResponse
import utils.{SpecHelper, WireMockServerHandler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps

class FatcaVoidConnectorISpec extends SpecHelper with WireMockServerHandler {

  lazy val app: Application = applicationBuilder()
    .configure(
      conf = "microservice.services.void-fatca-submission.port" -> server.port(),
      "auditing.enabled" -> "false"
    )
    .build()
  lazy val appConfig: AppConfig            = app.injector.instanceOf[AppConfig]
  lazy val connector: FatcaVoidConnector = app.injector.instanceOf[FatcaVoidConnector]
  lazy val url                        = s"/dac6/submitVoidRequest/v1"

  "FatcaVoidConnector" - {
    "Should return Future Success when EIS returns a 204 response with a valid response body" in {
      val requestPayload = voidRequestPayloadGen.sample.getOrElse(
        fail("Generator did not produce a value")
      )
      stubPostResponse(url, 204)
      val result = connector.submit(requestBody = requestPayload)
      result.futureValue mustBe ()
    }

    "Should return an Upstream Error Response when EIS returns a non‑200 response" in {
      val requestPayload = voidRequestPayloadGen.sample.getOrElse(
        fail("Generator did not produce a value")
      )
      stubPostResponse(url, 400)

      val ex =
        recoverToExceptionIf[UpstreamErrorResponse] {
          connector.submit(requestPayload)
        }.futureValue

      ex.statusCode mustBe 500
      ex.message mustBe "Unexpected response from EIS API: 400"
    }
  }

}
