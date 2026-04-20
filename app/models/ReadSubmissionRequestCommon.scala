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

package models


import SubmissionsConstants.{CRFA, RegimeType}
import play.api.libs.json.{Json, OFormat}

case class ReadSubmissionRequestCommon(
                           regime: RegimeType= CRFA,
                           originatingSystem: String = "MDTP",
                           transmittingSystem: String = "CADX",
                           requestParameters: Option[List[CommonParameters]] = None
                         )
object ReadSubmissionRequestCommon {
  implicit val format: OFormat[ReadSubmissionRequestCommon] = Json.format[ReadSubmissionRequestCommon]
}
