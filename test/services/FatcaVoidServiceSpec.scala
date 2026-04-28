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
import connectors.FatcaVoidConnector
import models.fatcavoid.VoidFatcaRequest
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.matchers.must.Matchers.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class FatcaVoidServiceSpec extends SpecBase {

  val connector: FatcaVoidConnector = mock[FatcaVoidConnector]
  val service: FatcaVoidService     = FatcaVoidService(connector)

  override def beforeEach(): Unit = {
    reset(connector)
    super.beforeEach()
  }

  "FatcaVoidService" - {
    "submit" - {

      "should return Future Success when Connector returns success" in {

        when(connector.submit(any())(any())).thenReturn(Future.successful(()))

        val result = service.submit("testSubscriptionId", VoidFatcaRequest(messageRefId = "testMessageRefId", fiid = "testFiID"))

        whenReady(result) {
          res => res mustBe ()
        }
      }

      "should fail when connector fails" in {

        when(connector.submit(any())(any())).thenReturn(Future.failed(new RuntimeException("failed")))

        val result = service.submit("testSubscriptionId", VoidFatcaRequest(messageRefId = "testMessageRefId", fiid = "testFiID"))

        whenReady(result.failed) {
          ex =>
            ex.getMessage mustBe "failed"
        }
      }

    }
  }

}
