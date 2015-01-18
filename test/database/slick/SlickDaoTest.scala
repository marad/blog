package database.slick

import org.joda.time.DateTime
import org.specs2.mock._
import org.specs2.mutable._
import org.specs2.mutable.Specification
import config.Config.dbDriver._

class SlickDaoTest extends Specification with Mockito {

  "Slick DAO" should {


    val dbMock = mock[Database]
    val postsTableMock = mock[TableQuery[PostTable]]

    "read single post by id" in {
      val db = new Db with DbTestData
      val daoUnderTest = new Dao(db)

      val first = daoUnderTest.readPost(1l)
      val fourth = daoUnderTest.readPost(4l)

      first should be equalTo Some(firstPost)
      fourth should be equalTo Some(fourthPost)
    }

  }

  private val firstPost = Post.tupled(DbTestData.firstPostData)
  private val fourthPost = Post.tupled(DbTestData.fourthPostData)
}
