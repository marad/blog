package database.dao

import database.{DatabaseTest, DbTestData}
import models.{Post, Tag}
import org.joda.time.DateTime
import org.scalatest.{FlatSpec, Matchers}

class PostsDaoTest extends FlatSpec with DatabaseTest with Matchers {

  "DAO" should "read single post by id" in withDao { daoUnderTest =>
    val first = daoUnderTest.findPost(1l)
    val fourth = daoUnderTest.findPost(4l)

    first shouldBe Some(PostsDaoTest.firstPost)
    fourth shouldBe Some(PostsDaoTest.fourthPost)
  }

  it should "insert post and return its id" in withDao { daoUnderTest =>
    val id: Long = daoUnderTest.savePost(PostsDaoTest.examplePost)
    val post = daoUnderTest.findPost(id).get
    post shouldBe PostsDaoTest.examplePost.copy(
      id = Some(id),
      created = post.created,
      updated = post.updated
    )
    id shouldBe post.id.get
  }

  it should "update post and return its id" in withDao { daoUnderTest =>
    val newContent = "Changed Content"
    val changedPost = PostsDaoTest.firstPost.copy(content = newContent)
    val id: Long = daoUnderTest.savePost(changedPost)
    val post = daoUnderTest.findPost(id).get

    post shouldBe changedPost.copy(
      created = post.created,
      updated = post.updated
    )
    id shouldBe post.id.get
  }

  it should "return None when reading non-existing post" in withDao { daoUnderTest =>
    daoUnderTest.findPost(12345) shouldBe None
  }

  it should "silently pass when deleting non-existing post" in withDao { daoUnderTest =>
    noException should be thrownBy daoUnderTest.deletePost(12345)
    daoUnderTest.deletePost(12345) shouldBe false
  }

  it should "delete post from database" in withDao { daoUnderTest =>
    val id = DbTestData.fifthPostData.id.get
    val deleted = daoUnderTest.deletePost(id)
    val post = daoUnderTest.findPost(id)
    val postTags = daoUnderTest.listTagsForPost(id)

    deleted shouldBe true
    post shouldBe None
    postTags shouldBe empty
  }

  it should "sort posts on first page descending by creation date" in withDao { daoUnderTest =>
    val publishedPosts = daoUnderTest.listPostsOnFirstPage(10)
    publishedPosts shouldBe PostsDaoTest.allPosts
      .filter(_.published)
      .sortWith { (a, b) => a.created.isAfter(b.created) }
  }

  it should "should paginate listing of posts sorted descending by creation date" in withDao { daoUnderTest =>
    val expectedAllPosts = PostsDaoTest.allPosts
      .sortWith { (a, b) => a.created.isAfter(b.created)}
      .drop(2).take(2)

    val expectedPublishedPosts = PostsDaoTest.allPosts
      .filter(_.published)
      .sortWith { (a, b) => a.created.isAfter(b.created)}
      .drop(2).take(2)

    val allPosts = daoUnderTest.listPosts(2, 2, publishedOnly = false)
    val publishedPosts = daoUnderTest.listPosts(2, 2)

    allPosts shouldBe expectedAllPosts
    publishedPosts shouldBe expectedPublishedPosts
  }

  it should "count posts" in withDao { daoUnderTest =>
    val count = daoUnderTest.postCount()
    count shouldBe PostsDaoTest.allPosts.size
  }

  it should "list posts from period sorted descending" in withDao { daoUnderTest =>
    val start = DbTestData.secondCreateTime
    val end = DbTestData.fourthCreateTime

    val allPosts = daoUnderTest.listsPostsForPeriod(start, end, publishedOnly = false)
    val publishedPosts = daoUnderTest.listsPostsForPeriod(start, end)

    allPosts shouldBe PostsDaoTest.allPosts
      .drop(1).take(3)
      .sortWith { (a, b) => a.created.isAfter(b.created) }

    publishedPosts shouldBe allPosts.filter(_.published)
  }

  it should "search by phrase for the post in title, extract, and content" in withDao { daoUnderTest =>
    val searchPhrase = "post"

    val posts = daoUnderTest.searchWithPhrase(0, 10, Seq(searchPhrase))
    posts shouldBe Seq(
      PostsDaoTest.fifthPost,
      PostsDaoTest.fourthPost,
      PostsDaoTest.firstPost
    )
  }

  it should "count all search results" in withDao { daoUnderTest =>
    val searchPhrases = Seq("post")
    val count = daoUnderTest.countAllSearchResults(searchPhrases)
    count shouldBe 3
  }

  it should "search with offset and max result size" in withDao { daoUnderTest =>
    val searchPhrase = "post"
    val posts = daoUnderTest.searchWithPhrase(1, 1, Seq(searchPhrase))

    posts shouldBe Seq(PostsDaoTest.fourthPost)
  }

  it should "search only in published posts" in withDao { daoUnderTest =>
    val searchPhrase = "published"
    val posts = daoUnderTest.searchWithPhrase(0, 10, Seq(searchPhrase))

    posts shouldBe Seq(PostsDaoTest.firstPost)
  }

  it should "search with multiple phrases" in withDao { daoUnderTest =>
    val searchPhrases = Seq("extract", "my")

    val posts = daoUnderTest.searchWithPhrase(0, 10, searchPhrases)

    posts shouldBe Seq(
      PostsDaoTest.fourthPost,
      PostsDaoTest.firstPost
    )
  }
}

object PostsDaoTest {
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

  val examplePost = new Post(None, "some title", "some extract", "content", new DateTime, new DateTime, true, postTags)

  val allPosts = Seq(firstPost, secondPost, thirdPost, fourthPost, fifthPost)
}
