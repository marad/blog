package database

import models.{Post, Tag}
import org.joda.time.DateTime
import org.scalamock.scalatest.MockFactory
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

class SlickDaoTest extends FlatSpec with Matchers with MockFactory with BeforeAndAfterEach {
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

  it should "search by phrase for the post in title, extract, and content" in withDatabase { daoUnderTest =>
    val searchPhrase = "post"

    val posts = daoUnderTest.searchWithPhrase(0, 10, Seq(searchPhrase))
    posts shouldBe Seq(
      SlickDaoTest.fifthPost,
      SlickDaoTest.fourthPost,
      SlickDaoTest.secondPost,
      SlickDaoTest.firstPost
    )
  }

  it should "count all search results" in withDatabase { daoUnderTest =>
    val searchPhrases = Seq("post")
    val count = daoUnderTest.countAllSearchResults(searchPhrases)
    count shouldBe 4
  }

  it should "search with offset and max result size" in withDatabase { daoUnderTest =>
    val searchPhrase = "post"
    val posts = daoUnderTest.searchWithPhrase(1, 1, Seq(searchPhrase))

    posts shouldBe Seq(SlickDaoTest.fourthPost)
  }

  it should "search with multiple phrases" in withDatabase { daoUnderTest =>
    val searchPhrases = Seq("extract", "my")

    val posts = daoUnderTest.searchWithPhrase(0, 10, searchPhrases)

    posts shouldBe Seq(
      SlickDaoTest.fourthPost,
      SlickDaoTest.firstPost
    )
  }
}

object SlickDaoTest {
  val firstTag = Tag.fromDbTag(DbTestData.firstTagData)
  val secondTag = Tag.fromDbTag(DbTestData.secondTagData)
  val thirdTag = Tag.fromDbTag(DbTestData.thirdTagData)
  val anotherTag = Tag(Some(4), "another tag")
  val postTags = Seq(firstTag, secondTag, anotherTag)

  val firstPost = Post.fromDbPostAndTags(DbTestData.firstPostData, Seq(firstTag, secondTag, thirdTag))
  val secondPost = Post.fromDbPostAndTags(DbTestData.secondPostData, Seq(firstTag, secondTag))
  val thirdPost = Post.fromDbPostAndTags(DbTestData.thirdPostData, Seq(thirdTag))
  val fourthPost = Post.fromDbPostAndTags(DbTestData.fourthPostData, Seq())
  val fifthPost = Post.fromDbPostAndTags(DbTestData.fifthPostData, Seq())

  val examplePost = new Post(None, "some title", "some extract", "content", new DateTime, new DateTime, postTags)

  val allPosts = Seq(firstPost, secondPost, thirdPost, fourthPost, fifthPost)
}
