package database.slick

import config.Config

import scala.slick.jdbc.JdbcBackend.Database
import scala.slick.lifted.TableQuery

class Db {
  val instance = Database.forURL(Config.dbUrl, driver=Config.dbDriverClass)

  val posts = TableQuery[PostTable]
  val tags = TableQuery[TagTable]
  val postTags = TableQuery[PostTagsTable]
}

