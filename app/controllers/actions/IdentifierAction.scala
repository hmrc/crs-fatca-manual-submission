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

package controllers.actions

import com.google.inject.Inject
import config.AppConfig
import models.IdentifierType
import models.request.IdentifierRequest
import play.api.Logging
import play.api.mvc.*
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.AuthProvider.GovernmentGateway
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals
import uk.gov.hmrc.auth.core.retrieve.~
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.http.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  config: AppConfig,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction
    with AuthorisedFunctions
    with Logging {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier = HeaderCarrierConverter.fromRequest(request)

    authorised(AuthProviders(GovernmentGateway) and ConfidenceLevel.L50)
      .retrieve(Retrievals.internalId and Retrievals.allEnrolments and Retrievals.affinityGroup) {
        case Some(internalId) ~ enrolments ~ Some(affinity) => getSubscriptionId(request, enrolments, internalId, affinity, block)
        case _                                              => throw AuthorisationException.fromString("Unable to retrieve internal id or affinity group")
      } recover {
      case _ =>
        throw AuthorisationException.fromString("Unable to retrieve internal id or affinity group")
    }
  }

  private def getSubscriptionId[A](request: Request[A],
                                   enrolments: Enrolments,
                                   internalId: String,
                                   affinityGroup: AffinityGroup,
                                   block: IdentifierRequest[A] => Future[Result]
  ): Future[Result] = {
    val subscriptionId: Option[String] = for {
      enrolment      <- enrolments.getEnrolment(config.enrolmentKey)
      id             <- enrolment.getIdentifier(IdentifierType.FATCAID)
      subscriptionId <- if (id.value.nonEmpty) Some(id.value) else None
    } yield subscriptionId

    subscriptionId.fold {
      Future.failed(AuthorisationException.fromString("Unable to retrieve internal id or affinity group"))
    } {
      fatcaId =>
        block(IdentifierRequest(request, internalId, fatcaId, affinityGroup))
    }
  }
}
