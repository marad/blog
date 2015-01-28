package database.slick

import org.joda.time.DateTime

case class DbPost(id: Option[Long], title: String, extract: String, content: String, created: DateTime, updated: DateTime) {
  def toTuple = (id, title, extract, content, created, updated)
}
case class DbTag(id: Option[Long], name: String)
case class DbPostTag(pid:Long, tid:Long)
