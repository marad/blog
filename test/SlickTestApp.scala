import database.slick.{PostTable, TagTable, PostTagsTable}
import org.joda.time.DateTime

import config.Config.dbDriver._
import database.slick.JodaSupport._

import database.slick.{PostTable, TagTable, PostTagsTable}


object SlickTestApp {

  def main(args: Array[String]) = {
    val posts = TableQuery[PostTable]
    val tags = TableQuery[TagTable]
    val postTags = TableQuery[PostTagsTable]
    val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")

    db withTransaction  { implicit session =>
      (posts.ddl ++ tags.ddl ++ postTags.ddl).create

      posts ++= Seq(
        (Some(1l), "Title 1", "Extract 1", "Content 1", new DateTime(), new DateTime()),
        (Some(2l), "Title 2", "Extract 2", "Content 2", new DateTime(), new DateTime()),
        (Some(3l), "Title 3", "Extract 3", "Content 3", new DateTime(), new DateTime()),
        (Some(4l), "Title 4", "Extract 4", "Content 4", new DateTime(), new DateTime()),
        (Some(5l), "Title 5", "Extract 5", "Content 5", new DateTime(), new DateTime())
      )

      tags ++= Seq(
        (Some(1l), "Tag 1"),
        (Some(2l), "Tag 2"),
        (Some(3l), "Tag 3")
      )

      postTags ++= Seq(
        (Some(1l), Some(1l)),
        (Some(1l), Some(2l)),
        (Some(1l), Some(3l)),
        (Some(2l), Some(1l)),
        (Some(2l), Some(2l)),
        (Some(3l), Some(3l))
      )

//      val tagsForPost = postTags.filter(_.postId === 1l).leftJoin(tags).list
      val tagsForPost = for {
        tp <- postTags.filter(_.postId === 1l)
        t <- tags if tp.tagId === t.id
      } yield t.*

      case class Tag(id: Option[Long], name: String)

      println(tagsForPost.list.map(Tag.tupled))
    }
  }

}
