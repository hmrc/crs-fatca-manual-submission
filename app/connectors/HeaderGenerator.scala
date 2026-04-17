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
