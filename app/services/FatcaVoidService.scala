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

import connectors.FatcaVoidConnector
import models.Constants.{EISTransmittingSystem, MDTPSystem}
import models.fatcavoid.*
import play.api.Logging
import uk.gov.hmrc.http.HeaderCarrier

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class FatcaVoidService @Inject (connector: FatcaVoidConnector) extends Logging {

  def submit(subscriptionId: String, voidFatcaRequest: VoidFatcaRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    connector.submit(toVoidRequestPayload(subscriptionId, voidFatcaRequest))

  private def toVoidRequestPayload(subscriptionId: String, request: VoidFatcaRequest): VoidRequestPayload =
    VoidRequestPayload(
      voidRequest = VoidRequest(
        requestCommon = RequestCommon(
          originatingSystem = MDTPSystem,
          transmittingSystem = EISTransmittingSystem,
          regime = Regime.FATCA,
          requestParameters = None
        ),
        requestDetails = RequestDetails(
          subscriptionId = subscriptionId,
          messageRefId = request.messageRefId,
          fiId = request.fiid
        )
      )
    )

}
