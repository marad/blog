package database.slick

import config.Config

import config.Config.dbDriver.simple._
import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.jdbc.meta.MTable
import scala.slick.lifted.TableQuery

class Db {
  val instance = Database.forURL(
    Config.db.url,
    user=Config.db.user,
    password=Config.db.password,
    driver=Config.db.driver)

  val posts = TableQuery[PostTable]
  val tags = TableQuery[TagTable]
  val postTags = TableQuery[PostTagsTable]


  instance.withTransaction { implicit session =>
    if (MTable.getTables("POSTS").list.isEmpty) posts.ddl.create
    if (MTable.getTables("TAGS").list.isEmpty) tags.ddl.create
    if (MTable.getTables("POST_TAGS").list.isEmpty) postTags.ddl.create
  }
}

