package database.slick

import config.Config.dbDriver._
import org.scalamock.scalatest.MockFactory
import org.scalatest.{Matchers, FlatSpec}

class SlickDaoTest extends FlatSpec with Matchers with MockFactory {

  val dbMock = mock[Database]
//  val postsTableMock = mock[TableQuery[PostTable]]

  "DAO" should "read single post by id" in {
//    val db = new Db with DbTestData
//    val daoUnderTest = new Dao(db)

//    val first = daoUnderTest.readPost(1l)
//    val fourth = daoUnderTest.readPost(4l)

//      first should be equalTo Some(firstPost)
//      fourth should be equalTo Some(fourthPost)
  }

//  private val firstPost = Post.tupled(DbTestData.firstPostData)
//  private val fourthPost = Post.tupled(DbTestData.fourthPostData)
}
