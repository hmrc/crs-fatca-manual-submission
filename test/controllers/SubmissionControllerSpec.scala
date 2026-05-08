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
import models.UserData
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.matchers.must.Matchers.{must, mustBe}
import play.api.Application
import play.api.http.Status.{NOT_FOUND, OK}
import play.api.inject.bind
import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers.{defaultAwaitTimeout, route, status, writeableOf_AnyContentAsEmpty, GET}
import repositories.SubmissionsRepository

import scala.concurrent.Future

class SubmissionControllerSpec extends SpecBase {

  val mockRepository: SubmissionsRepository = mock[SubmissionsRepository]

  override def beforeEach(): Unit = reset(mockRepository)

  val application: Application = applicationBuilder()
    .overrides(
      bind[SubmissionsRepository].toInstance(mockRepository),
      bind[IdentifierAction].to[FakeIdentifierAuthAction]
    )
    .build()

  "SubmissionController" - {
    "must return OK when repository returns user answers" in {

      val ua = """{
        |"_id":"testSubscriptionId",
        |"data":{"submissionsList":[
        |{"fiId":"1234567890","fiName":"Test FI Name","fileName":"testfilename","submissionStatus":"PASSED","uploadDateTime":"2025-02-28T10:20:56.789Z","regime":"CRS","reportingYear":"2016","submissionCaseId":"CRS-SUB-12224","submissionType":"XML","submissionFileType":"CRS702","messageRefId":"testfilename"},
        |{"fiId":"1234567890","fiName":"Test FI Name1","fileName":"testfilename1","submissionStatus":"PASSED","uploadDateTime":"2025-02-28T10:20:56.789Z","regime":"FATCA","reportingYear":"2016","submissionCaseId":"FATCA-SUB-12224","submissionType":"XML","submissionFileType":"FATCA2","messageRefId":"testfilename1","submissionDeleteStatus":true}]
        |},"lastUpdated":{"$date":{"$numberLong":"1777305419357"}}}""".stripMargin
      when(mockRepository.get(any())).thenReturn(Future.successful(Some(Json.parse(ua).as[UserData])))

      val request = FakeRequest(GET, routes.SubmissionController.get().url)

      val result = route(application, request).value
      status(result) mustBe OK
    }

    "must return not found when repository returns none" in {

      when(mockRepository.get(any())).thenReturn(Future.successful(None))

      val request = FakeRequest(GET, routes.SubmissionController.get().url)

      val result = route(application, request).value
      status(result) mustBe NOT_FOUND
    }
  }
}
