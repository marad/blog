package models

import org.joda.time.DateTime
import play.api.libs.json.Json

case class Post(title: String, short: String, content: String, date: DateTime, updated: DateTime, tags: Seq[Tag])

object Post {
  implicit val postFormat = Json.format[Post]
}
