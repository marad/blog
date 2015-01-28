package database.slick

import config.Config
import Config.dbDriver._
import org.joda.time.DateTime

trait DbTestData {
  val instance: Database
  val posts: TableQuery[PostTable]
  val tags: TableQuery[TagTable]
  val postTags: TableQuery[PostTagsTable]

  val connection = instance.createConnection()

  instance withSession { implicit session =>
    (posts.ddl ++ tags.ddl ++ postTags.ddl).create

    posts ++= Seq(
      DbTestData.firstPostData,
      DbTestData.secondPostData,
      DbTestData.thirdPostData,
      DbTestData.fourthPostData,
      DbTestData.fifthPostData
    )

    tags ++= Seq(
      DbTestData.firstTagData,
      DbTestData.secondTagData,
      DbTestData.thirdTagData
    )

    postTags ++= Seq(
      DbPostTag(1l, 1l),
      DbPostTag(1l, 2l),
      DbPostTag(1l, 3l),
      DbPostTag(2l, 1l),
      DbPostTag(2l, 2l),
      DbPostTag(3l, 3l)
    )

  }
}

object DbTestData {
  val updateTime = new DateTime()

  val firstCreateTime = new DateTime(1)
  val secondCreateTime = new DateTime(2)
  val thirdCreateTime = new DateTime(3)
  val fourthCreateTime = new DateTime(4)
  val fifthCreateTime = new DateTime(5)

  val firstPostData   = DbPost(Some(1l), "Title 1", "Extract 1", "Content 1", DbTestData.firstCreateTime, DbTestData.updateTime)
  val secondPostData  = DbPost(Some(2l), "Title 2", "Extract 2", "Content 2", DbTestData.secondCreateTime, DbTestData.updateTime)
  val thirdPostData   = DbPost(Some(3l), "Title 3", "Extract 3", "Content 3", DbTestData.thirdCreateTime, DbTestData.updateTime)
  val fourthPostData  = DbPost(Some(4l), "Title 4", "Extract 4", "Content 4", DbTestData.fourthCreateTime, DbTestData.updateTime)
  val fifthPostData   = DbPost(Some(5l), "Title 5", "Extract 5", "Content 5", DbTestData.fifthCreateTime, DbTestData.updateTime)

  val firstTagData = DbTag(Some(1l), "Tag 1")
  val secondTagData = DbTag(Some(2l), "Tag 2")
  val thirdTagData = DbTag(Some(3l), "Tag 3")
}