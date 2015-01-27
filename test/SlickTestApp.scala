import config.Config.dbDriver._
import database.slick._
import org.joda.time.DateTime


object SlickTestApp {

  def main(args: Array[String]) = {
    val posts = TableQuery[PostTable]
    val tags = TableQuery[TagTable]
    val postTags = TableQuery[PostTagsTable]
    val db = Database.forURL("jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1", driver="org.h2.Driver")

    db withTransaction  { implicit session =>
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

      println(tagsForPost.list)
//      println(tagsForPost.list.map(Tag.tupled))
    }
  }

}
