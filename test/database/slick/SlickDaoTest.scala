package database.slick

import models.{Post, Tag}
import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class SlickDaoTest extends FlatSpec with Matchers with MockFactory with BeforeAndAfterEach {
  // TODO: usuwanie posta ktorego nie ma
  // TODO: czytanie posta ktorego nie ma
  // TODO: postcount

  def withDatabase(testCode: Dao => Any) {
    val db = new Db with DbTestData
    val daoUnderTest = new Dao(db)

    try {
      testCode(daoUnderTest)
    }
    finally db.connection.close()
  }

  "DAO" should "read single post by id" in withDatabase { daoUnderTest =>
    val first = daoUnderTest.readPost(1l)
    val fourth = daoUnderTest.readPost(4l)

    first shouldBe Some(SlickDaoTest.firstPost)
    fourth shouldBe Some(SlickDaoTest.fourthPost)
  }

  it should "write single post and return its id" in withDatabase { daoUnderTest =>
    val id: Long = daoUnderTest.writePost(SlickDaoTest.examplePost)
    val post = daoUnderTest.readPost(id).get
    post shouldBe SlickDaoTest.examplePost.copy(id = Some(id))
  }

  it should "delete post from database" in withDatabase { daoUnderTest =>
    val id = DbTestData.fifthPostData.id.get
    daoUnderTest.deletePost(id)
    val post = daoUnderTest.readPost(id)
    val postTags = daoUnderTest.listTagsForPost(id)
    post shouldBe None
    postTags shouldBe empty
  }

  it should "list posts from database and sort by creation date" in withDatabase { daoUnderTest =>
    val posts = daoUnderTest.listPosts()
    posts shouldBe SlickDaoTest.allPosts.sortWith { (a, b) => a.created.isAfter(b.created) }
  }

  it should "should paginate listing sorted by creation date" in withDatabase { daoUnderTest =>
    val posts = daoUnderTest.listPosts(2, 2)
    val expectedPosts = SlickDaoTest.allPosts
      .sortWith { (a, b) => a.created.isAfter(b.created) }
      .drop(2).take(2)

    posts shouldBe expectedPosts
  }
}

object SlickDaoTest {
  val firstPost = Post.fromDbPostAndTags(
    DbTestData.firstPostData, Seq(
      Tag.fromDbTag(DbTestData.firstTagData),
      Tag.fromDbTag(DbTestData.secondTagData),
      Tag.fromDbTag(DbTestData.thirdTagData)
    )
  )
  val fourthPost = Post.fromDbPostAndTags(DbTestData.fourthPostData, Seq())

  val firstTag = Tag.fromDbTag(DbTestData.firstTagData)
  val secondTag = Tag.fromDbTag(DbTestData.secondTagData)
  val thirdTag = Tag.fromDbTag(DbTestData.thirdTagData)
  val anotherTag = Tag(Some(4), "another tag")
  val postTags = Seq(firstTag, secondTag, anotherTag)
  val examplePost = new Post(None, "some title", "some extract", "content",
    new DateTime, new DateTime, postTags)

  val allPosts = Seq(
    Post.fromDbPostAndTags(DbTestData.firstPostData, Seq(firstTag, secondTag, thirdTag)),
    Post.fromDbPostAndTags(DbTestData.secondPostData, Seq(firstTag, secondTag)),
    Post.fromDbPostAndTags(DbTestData.thirdPostData, Seq(thirdTag)),
    Post.fromDbPostAndTags(DbTestData.fourthPostData, Seq()),
    Post.fromDbPostAndTags(DbTestData.fifthPostData, Seq())
  )
}
