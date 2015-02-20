package database

import config.Config.dbDriver.simple._
import JodaSupport.simple._
import database.model.{DbAccount, DbTag, DbPostTag, DbPost}
import org.joda.time.DateTime

class PostTable(tag: Tag)
  extends Table[DbPost](tag, "POSTS") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def title = column[String]("TITLE", O.DBType("VARCHAR(250)"))
  def extract = column[String]("EXTRACT", O.DBType("TEXT"))
  def content = column[String]("CONTENT", O.DBType("TEXT"))
  def created = column[DateTime]("CREATED")
  def updated = column[DateTime]("UPDATED")
  def published = column[Boolean]("PUBLISHED")

  override def * = (id.?, title, extract, content, created, updated, published) <> ( DbPost.tupled, DbPost.unapply)
}

class TagTable(tag: Tag)
  extends Table[DbTag](tag, "TAGS") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME", O.DBType("VARCHAR(100)"))

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

  override def * = (postId, tagId) <> (DbPostTag.tupled, DbPostTag.unapply)
}

class AccountTable(t: Tag)
  extends Table[DbAccount](t, "ACCOUNTS") {

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def username = column[String]("USERNAME", O.DBType("VARCHAR(50)"))
  def password = column[String]("PASSWORD", O.DBType("VARCHAR(80)"))

  override def * = (id.?, username, password) <> (DbAccount.tupled, DbAccount.unapply)
}
