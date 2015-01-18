package database.slick

import org.joda.time.DateTime

import scala.slick.driver.H2Driver.simple._
import database.slick.JodaSupport._

class PostTable(tag: Tag)
  extends Table[(Option[Long], String, String, String, DateTime, DateTime)](tag, "POSTS") {

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def title = column[String]("TITLE")
  def extract = column[String]("EXTRACT")
  def content = column[String]("CONTENT")
  def created = column[DateTime]("CREATED")
  def updated = column[DateTime]("UPDATED")

  override def * = (id, title, extract, content, created, updated)
}

class TagTable(tag: Tag)
  extends Table[(Option[Long], String)](tag, "TAGS") {

  def id = column[Option[Long]]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")

  override def * = (id, name)
}

class PostTagsTable(t: Tag)
  extends Table[(Option[Long], Option[Long])](t, "POST_TAGS") {

  def postId = column[Option[Long]]("POST_ID")
  def tagId = column[Option[Long]]("TAG_ID")

  def post = foreignKey("POST_FK", postId, TableQuery[PostTable])(_.id, onDelete=ForeignKeyAction.Cascade)
  def tag = foreignKey("TAG_FK", tagId, TableQuery[TagTable])(_.id, onDelete=ForeignKeyAction.Cascade)

  def idx = index("INDEX", (postId, tagId), unique = true)

  override def * = (postId, tagId)
}
