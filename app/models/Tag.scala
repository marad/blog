package models

import database.slick.DbTag
import play.api.libs.json.Json


case class Tag(id: Option[Long], name: String) {
  def toDbTag: DbTag = DbTag(id, name)
}

object Tag {
  def tupled(t: (Option[Long], String)): Tag = new Tag(t._1, t._2)
  def fromDbTag(dbTag: DbTag): Tag = new Tag(dbTag.id, dbTag.name)

  implicit val tagFormat = Json.format[Tag]
}

