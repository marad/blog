package database.slick

import models.{Post, Tag}
import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class SlickDaoTest extends FlatSpec with Matchers with MockFactory with BeforeAndAfterEach {
  // TODO: check if writing second post with exising tag doesnt add another tag

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

  it should "insert post and return its id" in withDatabase { daoUnderTest =>
    val id: Long = daoUnderTest.writePost(SlickDaoTest.examplePost)
    val post = daoUnderTest.readPost(id).get
    post shouldBe SlickDaoTest.examplePost.copy(id = Some(id))
    id shouldBe post.id.get
  }

  it should "update post and return its id" in withDatabase { daoUnderTest =>
    val newContent = "Changed Content"
    val changedPost = SlickDaoTest.firstPost.copy(content = newContent)
    val id: Long = daoUnderTest.writePost(changedPost)
    val post = daoUnderTest.readPost(id).get

    post shouldBe changedPost
    id shouldBe post.id.get
  }

  it should "return None when reading non-existing post" in withDatabase { daoUnderTest =>
    daoUnderTest.readPost(12345) shouldBe None
  }

  it should "silently pass when deleting non-existing post" in withDatabase { daoUnderTest =>
    noException should be thrownBy daoUnderTest.deletePost(12345)
    daoUnderTest.deletePost(12345) shouldBe false
  }

  it should "delete post from database" in withDatabase { daoUnderTest =>
    val id = DbTestData.fifthPostData.id.get
    val deleted = daoUnderTest.deletePost(id)
    val post = daoUnderTest.readPost(id)
    val postTags = daoUnderTest.listTagsForPost(id)

    deleted shouldBe true
    post shouldBe None
    postTags shouldBe empty
  }

  it should "list posts from database and sort descending by creation date" in withDatabase { daoUnderTest =>
    val posts = daoUnderTest.listPostsOnFirstPage()
    posts shouldBe SlickDaoTest.allPosts.sortWith { (a, b) => a.created.isAfter(b.created) }
  }

  it should "should paginate listing sorted descending by creation date" in withDatabase { daoUnderTest =>
    val posts = daoUnderTest.listPosts(2, 2)
    val expectedPosts = SlickDaoTest.allPosts
      .sortWith { (a, b) => a.created.isAfter(b.created) }
      .drop(2).take(2)

    posts shouldBe expectedPosts
  }

  it should "count posts" in withDatabase { daoUnderTest =>
    val count = daoUnderTest.postCount()
    count shouldBe SlickDaoTest.allPosts.size
  }

  it should "list posts from period sorted descending" in withDatabase { daoUnderTest =>
    val start = DbTestData.secondCreateTime
    val end = DbTestData.fourthCreateTime

    val posts = daoUnderTest.listsPostsForPeriod(start, end)
    posts shouldBe SlickDaoTest.allPosts
      .drop(1).take(3)
      .sortWith { (a, b) => a.created.isAfter(b.created) }
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
    firstPost,
    Post.fromDbPostAndTags(DbTestData.secondPostData, Seq(firstTag, secondTag)),
    Post.fromDbPostAndTags(DbTestData.thirdPostData, Seq(thirdTag)),
    Post.fromDbPostAndTags(DbTestData.fourthPostData, Seq()),
    Post.fromDbPostAndTags(DbTestData.fifthPostData, Seq())
  )
}
