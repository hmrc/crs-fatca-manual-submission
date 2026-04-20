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

package connectors

import java.time.format.DateTimeFormatter
import java.time.ZonedDateTime
import java.time.ZoneId
import java.util.UUID

object HeaderGenerator {

  private val dateFormatter: DateTimeFormatter =
    DateTimeFormatter
      .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'UTC'")
      .withZone(ZoneId.of("UTC"))

  def accept(contentType: String = "application/json"): (String, String) =
    "Accept" -> contentType

  def contentType(contentType: String = "application/json"): (String, String) =
    "Content-Type" -> contentType

  def date: (String, String) =
    "Date" -> dateFormatter.format(ZonedDateTime.now(ZoneId.of("UTC")))

  def xConversationId: (String, String) =
    "x-conversation-id" -> UUID.randomUUID().toString
  
  def xForwardedHost: (String, String) =
    "x-forwarded-host" -> "MDTP"

  def defaultHeaders(authHeader: String, correlationID: UUID): Seq[(String, String)] =
    Seq(
      accept(),
      "Authorization" -> authHeader,
      "x-correlation-id" -> correlationID.toString,
      contentType(),
      date,
      xConversationId,
      xForwardedHost
    )
}
