package database.slick

import config.Config.dbDriver._
import database.slick.JodaSupport._
import org.joda.time.DateTime

import scala.slick.lifted.ProvenShape

class PostTable(tag: Tag)
  extends Table[DbPost](tag, "POSTS") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def title = column[String]("TITLE")
  def extract = column[String]("EXTRACT")
  def content = column[String]("CONTENT")
  def created = column[DateTime]("CREATED")
  def updated = column[DateTime]("UPDATED")

  override def * = (id.?, title, extract, content, created, updated) <> ( DbPost.tupled, DbPost.unapply)
}

class TagTable(tag: Tag)
  extends Table[DbTag](tag, "TAGS") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")

  def uniqueName = index("NAME_IS_UNIQUE", name, unique = true)

  override def * = (id.?, name) <> ( DbTag.tupled, DbTag.unapply)
}

class PostTagsTable(t: Tag)
  extends Table[DbPostTag](t, "POST_TAGS") {

  def postId = column[Long]("POST_ID")
  def tagId = column[Long]("TAG_ID")

  def post = foreignKey("POST_FK", postId.?, TableQuery[PostTable])(_.id, onDelete=ForeignKeyAction.Cascade)
  def tag = foreignKey("TAG_FK", tagId.?, TableQuery[TagTable])(_.id, onDelete=ForeignKeyAction.Cascade)

  def idx = index("INDEX", (postId, tagId), unique = true)

  override def * : ProvenShape[DbPostTag] = (postId, tagId) <> (DbPostTag.tupled, DbPostTag.unapply)
}
