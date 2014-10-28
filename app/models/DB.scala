package models

import sorm._

object Db extends Instance(
  entities = Set(
    Entity[Post](),
    Entity[Tag](unique = Set() + Seq("name")),
    Entity[TagUsage]()
  ),
//  url = "jdbc:postgresql://localhost/blog",
//  user = "blog",
//  password = "",
  url = "jdbc:h2:file:/home/morti/dev/scala/blog2/test.db",
  user = "",
  password = "",
  initMode = InitMode.Create
)
