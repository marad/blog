package models

import play.api.libs.json.Json

case class Tag(name: String)

object Tag {
  implicit val tagFormat = Json.format[Tag]
}
