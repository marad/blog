package models

import org.joda.time.DateTime
import play.api.libs.json.Json
import sorm.Persisted

case class Post(title: String, short: String, content: String, date: DateTime, updated: DateTime, tags: Seq[Tag]) {
  def tagsJoined = tags.map({ _.name }).fold(""){ (l: String, r: String) => r + ", " + l }

  def copyPost(p: Post): Post =
    this.copy(
      title = p.title,
      short = p.short,
      content = p.content,
      date = p.date,
      updated = p.updated,
      tags = p.tags
    )
}

object Post {
  implicit val postFormat = Json.format[Post]
}
