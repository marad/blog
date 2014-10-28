package models

import org.joda.time.DateTime
import play.api.libs.json.Json

case class Post(title: String, short: String, content: String, date: DateTime, tags: Seq[Tag]) {
  def tagsJoined = tags.map({ _.name }).fold(""){ (l: String, r: String) => r + ", " + l }
}

object Post {
  implicit val postFormat = Json.format[Post]
}
