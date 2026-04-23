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

package base

import org.scalatest.*
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.*
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Configuration
import play.api.inject.Injector
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import testutils.TestGenerators
import uk.gov.hmrc.http.HeaderCarrier

import java.time.{Clock, Instant, ZoneId}

trait SpecBase
    extends AnyFreeSpec
    with Matchers
    with GuiceOneAppPerSuite
    with OptionValues
    with EitherValues
    with TryValues
    with ScalaFutures
    with MockitoSugar
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with TestGenerators {

  def injector: Injector = app.injector

  def fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  implicit val hc: HeaderCarrier = HeaderCarrier()

  implicit val fixedClock: Clock = Clock.fixed(Instant.now(), ZoneId.of("UTC"))

  protected def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure(
        Configuration("metrics.enabled" -> "false", "enrolmentKeys.crsfatca.key" -> "HMRC-FATCA-ORG", "enrolmentKeys.crsfatca.identifier" -> "FATCAID")
      )
      .overrides()

}
