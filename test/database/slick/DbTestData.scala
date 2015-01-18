package database.slick

import config.Config
import Config.dbDriver._
import org.joda.time.DateTime

trait DbTestData {
  val instance: Database
  val posts: TableQuery[PostTable]
  val tags: TableQuery[TagTable]
  val postTags: TableQuery[PostTagsTable]

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
      (Some(1l), Some(1l)),
      (Some(1l), Some(2l)),
      (Some(1l), Some(3l)),
      (Some(2l), Some(1l)),
      (Some(2l), Some(2l)),
      (Some(3l), Some(3l))
    )

  }
}

object DbTestData {
  val updateTime = new DateTime()
  val createTime = new DateTime()

  val firstPostData   = (Some(1l), "Title 1", "Extract 1", "Content 1", DbTestData.createTime, DbTestData.updateTime)
  val secondPostData  = (Some(2l), "Title 2", "Extract 2", "Content 2", DbTestData.createTime, DbTestData.updateTime)
  val thirdPostData   = (Some(3l), "Title 3", "Extract 3", "Content 3", DbTestData.createTime, DbTestData.updateTime)
  val fourthPostData  = (Some(4l), "Title 4", "Extract 4", "Content 4", DbTestData.createTime, DbTestData.updateTime)
  val fifthPostData   = (Some(5l), "Title 5", "Extract 5", "Content 5", DbTestData.createTime, DbTestData.updateTime)

  val firstTagData = (Some(1l), "Tag 1")
  val secondTagData = (Some(2l), "Tag 2")
  val thirdTagData = (Some(3l), "Tag 3")
}