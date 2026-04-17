package models

import play.api.libs.json.{Json, OFormat}

case class CommonParameters(
                             paramName: String,
                             paramValue: String
                           )


object CommonParameters {
  implicit val format: OFormat[CommonParameters] = Json.format[CommonParameters]
}
