package slickmodels

import database.slick.DbPost
import org.joda.time.DateTime

case class Post(id: Option[Long], title: String, extract: String, content: String, created: DateTime, updated: DateTime, tags: Seq[Tag]) {
  def toDbPost: DbPost = DbPost(id, title, extract, content, created, updated)
}


