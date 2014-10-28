package models

import play.api.libs.json.Json

case class TagUsage(name: String, count: Long)

object TagUsage {
  implicit val tagUsageFormat = Json.format[TagUsage]
}
