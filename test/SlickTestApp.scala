import config.Config
import database._
import database.model.{DbTag, DbPostTag, DbPost}
import org.joda.time.DateTime
import config.Config.dbDriver.simple._

import models.Tag


object SlickTestApp {

  def main(args: Array[String]) = {
    val posts = TableQuery[PostTable]
    val tags = TableQuery[TagTable]
    val postTags = TableQuery[PostTagsTable]
    val accounts = TableQuery[AccountTable]
    val db = Database.forURL(
      Config.db.url,
      user=Config.db.user,
      password=Config.db.password,
      driver=Config.db.driver)

    db withTransaction  { implicit session =>
      for( stmt <- accounts.ddl.createStatements) println(stmt)
      for( stmt <- accounts.ddl.dropStatements) println(stmt)

      (posts.ddl ++ tags.ddl ++ postTags.ddl).create

      posts ++= Seq(
        DbPost(Some(1l), "Title 1", "Extract 1", "Content 1", new DateTime(), new DateTime()),
        DbPost(Some(2l), "Title 2", "Extract 2", "Content 2", new DateTime(), new DateTime()),
        DbPost(Some(3l), "Title 3", "Extract 3", "Content 3", new DateTime(), new DateTime()),
        DbPost(Some(4l), "Title 4", "Extract 4", "Content 4", new DateTime(), new DateTime()),
        DbPost(Some(5l), "Title 5", "Extract 5", "Content 5", new DateTime(), new DateTime())
      )

      tags ++= Seq(
        DbTag(None, "Tag 1"),
        DbTag(None, "Tag 2"),
        DbTag(None, "Tag 3")
      )

      postTags ++= Seq(
        DbPostTag(1l, 1l),
        DbPostTag(1l, 2l),
        DbPostTag(1l, 3l),
        DbPostTag(2l, 1l),
        DbPostTag(2l, 2l),
        DbPostTag(3l, 3l)
      )

//      val tagsForPost = postTags
//        .filter(_.postId === 1l)
//        .leftJoin(tags)
//        .map(_._2)

      val tagsForPost = for {
        tp <- postTags.filter(_.postId === 1l)
        t <- tags if tp.tagId === t.id
      } yield t

      val tagSeq = Seq(new Tag(None, "Hello"), new Tag(None, "Tag 1"))
      val tagNames = tagSeq.map(_.name)

      // THIS IS WHAT I WANT
      val tagsQuery = tags.filter(_.name inSet tagNames)


//      val tagsQuery = for {
//        tag <- tags.filter(_.name inSet tagNames)
//      } yield tag

      val testTags = tags.leftJoin(tags)

//      println(tagsForPost.list)
      println(tagsQuery.list)
//      println(tagsForPost.list.map(Tag.tupled))
    }
  }

}
