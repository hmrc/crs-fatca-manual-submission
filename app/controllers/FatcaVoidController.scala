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

import models.fatcavoid.VoidFatcaRequest
import play.api.mvc.{Action, ControllerComponents}
import services.FatcaVoidService
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController
import com.google.inject.{Inject, Singleton}
import controllers.actions.IdentifierAction

import scala.concurrent.ExecutionContext

@Singleton
class FatcaVoidController @Inject() (
  cc: ControllerComponents,
  service: FatcaVoidService,
  identifierAction: IdentifierAction
)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def submit(): Action[VoidFatcaRequest] = identifierAction.async(parse.json[VoidFatcaRequest]) {
    implicit request =>
      service
        .submit(request.fatcaId, request.body)
        .map(
          _ => Ok
        )
        .recover(
          _ => InternalServerError
        )
  }
}
