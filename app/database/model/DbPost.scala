package database.model

import org.joda.time.DateTime

case class DbPost(id: Option[Long], title: String, extract: String, content: String, created: DateTime, updated: DateTime) {
  def toTuple = (id, title, extract, content, created, updated)
}
