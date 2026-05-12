/*
 * Copyright 2024 HM Revenue & Customs
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

import base.SpecBase
import com.google.inject.Inject
import config.AppConfig
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.matchers.must.Matchers.mustBe
import play.api.inject
import play.api.inject.bind
import play.api.mvc.{Action, AnyContent, BodyParsers, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import testutils.RetrievalOps.Ops
import uk.gov.hmrc.auth.core.*
import uk.gov.hmrc.auth.core.AffinityGroup.Organisation
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class AuthenticatedIdentifierActionSpec extends SpecBase {

  class Harness(authAction: IdentifierAction) {

    def onPageLoad(): Action[AnyContent] = authAction { _ =>
      Results.Ok
    }
  }

  val mockAuthConnector: AuthConnector     = mock[AuthConnector]
  val bodyParsers: BodyParsers.Default     = app.injector.instanceOf[BodyParsers.Default]
  val appConfig: AppConfig = app.injector.instanceOf[AppConfig]

  type AuthRetrievals = Option[String] ~ Enrolments ~ Option[AffinityGroup]

  "Auth Action" - {

    "must throw Exception when the user is not logged in " in {

      val application = applicationBuilder()
        .overrides(
          bind[AuthConnector].toInstance(mockAuthConnector)
        )
        .build()

      running(application) {
        val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(new MissingBearerToken), appConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())
        whenReady(result.failed) {
          ex => ex.getMessage mustBe "Unable to retrieve internal id or affinity group"
        }
      }
    }

    "must retrieve details when user is logged in" in {

      val application = applicationBuilder()
        .overrides(
          bind[AuthConnector].toInstance(mockAuthConnector)
        )
        .build()
      val enrolments: Set[Enrolment] = Set(
        Enrolment("HMRC-FATCA-ORG", Seq(EnrolmentIdentifier("FATCAID","XE3ATCA0009234567")),"Activated")
      )
      val validRetrievals: AuthRetrievals = Some("userId") ~ Enrolments(enrolments) ~ Some(Organisation)

      running(application) {
        when(mockAuthConnector.authorise[AuthRetrievals](any(), any())(any(), any()))
          .thenReturn(Future.successful(validRetrievals))
        val authAction = new AuthenticatedIdentifierAction(mockAuthConnector, appConfig, bodyParsers)
        val controller = new Harness(authAction)
        val result = controller.onPageLoad()(FakeRequest())
        status(result) mustBe OK
      }
    }
  }
}

class FakeFailingAuthConnector @Inject() (exceptionToReturn: Throwable) extends AuthConnector {
  val serviceUrl: String = ""

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[A] =
    Future.failed(exceptionToReturn)
}
