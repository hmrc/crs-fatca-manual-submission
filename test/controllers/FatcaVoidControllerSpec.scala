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

package controllers

import base.SpecBase
import controllers.actions.{FakeIdentifierAuthAction, IdentifierAction}
import models.fatcavoid.VoidFatcaRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.matchers.must.Matchers.{must, mustBe}
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, route, status, writeableOf_AnyContentAsJson, POST}
import services.FatcaVoidService
import uk.gov.hmrc.http.InternalServerException

import scala.concurrent.Future

class FatcaVoidControllerSpec extends SpecBase {

  val mockService: FatcaVoidService = mock[FatcaVoidService]

  override def beforeEach(): Unit = reset(mockService)

  val application: Application = applicationBuilder()
    .overrides(
      bind[FatcaVoidService].toInstance(mockService),
      bind[IdentifierAction].to[FakeIdentifierAuthAction]
    )
    .build()

  "FatcaVoidController" - {
    "must return OK when service successfully void the request" in {

      val requestBody = VoidFatcaRequest("testMessageRefId", "testFiid")
      when(mockService.submit(any(), any())(any(), any())).thenReturn(Future.successful(()))

      val request = FakeRequest(POST, routes.FatcaVoidController.submit().url)
        .withJsonBody(Json.toJson(requestBody))

      val result = route(application, request).value
      status(result) mustBe OK
    }

    "must throw exception when service throws exception" in {

      when(mockService.submit(any(), any())(any(), any())).thenReturn(Future.failed(InternalServerException("Failed")))

      val requestBody = VoidFatcaRequest("testMessageRefId", "testFiid")
      val request     = FakeRequest(POST, routes.FatcaVoidController.submit().url).withJsonBody(Json.toJson(requestBody))

      val result = route(application, request).value
      status(result) mustBe INTERNAL_SERVER_ERROR
    }
  }
}
