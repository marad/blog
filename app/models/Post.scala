package models

import database.model.DbPost
import org.joda.time.DateTime
import play.api.libs.json.Json

case class Post(
                 id: Option[Long],
                 title: String,
                 extract: String,
                 content: String,
                 created: DateTime,
                 updated: DateTime,
                 published: Boolean,
                 tags: Seq[Tag]) {
  def toDbPost: DbPost = DbPost(id, title, extract, content, created, updated, published)
}

object Post {
  implicit val postFormat = Json.format[Post]

  def fromDbPostAndTags(dbPost: DbPost, tags: Seq[Tag]): Post =
    Post(
      dbPost.id,
      dbPost.title,
      dbPost.extract,
      dbPost.content,
      dbPost.created,
      dbPost.updated,
      dbPost.published,
      tags)
}


