/*
 * Copyright 2025 HM Revenue & Customs
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

import base.SpecBase
import connectors.SubmissionsConnector
import models.{RequestSubmissionHistoryParameters, UserData}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.matchers.must.Matchers.*
import repositories.SubmissionsRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class SubmissionServiceSpec extends SpecBase {

  val repository: SubmissionsRepository = mock[SubmissionsRepository]
  val connector: SubmissionsConnector   = mock[SubmissionsConnector]
  val service: SubmissionService        = SubmissionService(connector, repository)

  override def beforeEach(): Unit = {
    reset(repository, connector)
    super.beforeEach()
  }

  "SubmissionService" - {
    "readAndMaybeCache" - {

      "should return submissions and cache when shouldCache is true" in {

        val connectorResponse = readSubmissionResponseGen.sample.get
        when(connector.readSubmission(any())(any())).thenReturn(Future.successful(connectorResponse))
        when(repository.set(any())).thenReturn(Future.successful(true))

        val result = service.readAndMaybeCache(RequestSubmissionHistoryParameters(true, None), "subscriptionId")

        whenReady(result) {
          res =>
            res mustBe connectorResponse.submissionsListResponse.responseDetails
            verify(repository).set(any[UserData])
            verify(connector).readSubmission(any())(any)
        }
      }

      "should fail when repository.set fails and caching is enabled" in {

        val repoException     = new RuntimeException("Repository failure")
        val connectorResponse = readSubmissionResponseGen.sample.get

        when(connector.readSubmission(any())(any())).thenReturn(Future.successful(connectorResponse))
        when(repository.set(any())).thenReturn(Future.failed(repoException))

        val result = service.readAndMaybeCache(RequestSubmissionHistoryParameters(true, None), "subscriptionId")

        whenReady(result.failed) {
          ex =>
            ex mustBe repoException
        }

        verify(connector).readSubmission(any())(any())
        verify(repository).set(any())
      }

      "should return submissions and not cache when shouldCache is false" in {

        val connectorResponse = readSubmissionResponseGen.sample.get
        when(connector.readSubmission(any())(any())).thenReturn(Future.successful(connectorResponse))

        val result = service.readAndMaybeCache(RequestSubmissionHistoryParameters(false, None), "subscriptionId")

        whenReady(result) {
          res =>
            res mustBe connectorResponse.submissionsListResponse.responseDetails
            verify(repository, never()).set(any())
            verify(connector).readSubmission(any())(any())
        }
      }

      "should fail when connector fails" in {

        when(connector.readSubmission(any())(any())).thenReturn(Future.failed(new RuntimeException("failed")))

        val result = service.readAndMaybeCache(RequestSubmissionHistoryParameters(false, None), "subscriptionId")

        whenReady(result.failed) {
          ex =>
            ex.getMessage mustBe "failed"
        }
      }

    }
  }

}
