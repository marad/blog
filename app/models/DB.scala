package models

import sorm._
import play.api.Play

object Db extends Instance(
  entities = Set(
    Entity[Post](),
    Entity[Tag](unique = Set() + Seq("name")),
    Entity[TagUsage](),
    Entity[Account](unique = Set() + Seq("name", "email"))
  ),
  url = 
    Play.current.configuration.getString("database.url") match {
      case Some(value) => value
      case None => "jdbc:h2:file:/home/morti/dev/scala/blog2/test.db"
    },
  user =
    Play.current.configuration.getString("database.user") match {
      case Some(value) => value
      case None => ""
    },
  password =
    Play.current.configuration.getString("database.password") match {
      case Some(value) => value
      case None => ""
    },
//  url = "jdbc:h2:file:/home/morti/dev/scala/blog2/test.db",
//  user = "",
//  password = "",
  initMode = InitMode.Create


)
