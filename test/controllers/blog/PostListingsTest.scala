package controllers.blog

import controllers.ControllerSpec
import database.{Dao, DbTestData}
import models.Post
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}
import play.api.mvc.Controller
import play.api.test.FakeRequest

class PostListingsTest extends WordSpec with ControllerSpec with Matchers with MockitoSugar
with TableDrivenPropertyChecks  {

  class TestController(val dao: Dao, val pager: Pager)
    extends Controller with PostListings {
    val postsPerPage = 2
  }

  val daoMock: Dao = mock[Dao]
  val pagerMock: Pager = new Pager(2)
  val controller: PostListings = new TestController(daoMock, pagerMock)
  val posts = Seq(
    Post.fromDbPostAndTags(DbTestData.firstPostData, Seq()),
    Post.fromDbPostAndTags(DbTestData.secondPostData, Seq()),
    Post.fromDbPostAndTags(DbTestData.thirdPostData, Seq()),
    Post.fromDbPostAndTags(DbTestData.fourthPostData, Seq()),
    Post.fromDbPostAndTags(DbTestData.fifthPostData, Seq())
  )

  val request = FakeRequest("GET", "/ignored")
  when(daoMock.postCount()) thenReturn 5
  when(daoMock.listPosts(0, 2)) thenReturn posts.take(2)
  when(daoMock.listPosts(2, 2)) thenReturn posts.drop(2).take(2)
  when(daoMock.listPosts(4, 2)) thenReturn posts.drop(4).take(2)
  when(daoMock.countAllSearchResults(any())) thenReturn 3
  when(daoMock.searchWithPhrase(0, 2, Seq("search"))) thenReturn posts.take(2)
  when(daoMock.searchWithPhrase(2, 2, Seq("search"))) thenReturn posts.drop(2).take(1)

  "Listing" should {
    "return index page" in {
      val result = controller.listIndex(request)
      status(result) shouldBe OK
      contentAsString(result) should include(posts(0).title)
      contentAsString(result) should include(posts(1).title)
    }

    "return pages proper pages by id" in {
      val cases = Table("page", 1, 2, 3)
      forAll(cases) { page =>
        val firstPostOnPage = posts((page-1) * 2)
        val result = controller.list(page)(request)
        status(result) shouldBe OK
        contentAsString(result) should include(firstPostOnPage.title)
      }
    }

    "return not found when invalid page is requested" in {
      val cases = Table("page", -1, 0, 4, 5, 6)
      forAll(cases) { page =>
        val result = controller.list(page)(request)
        status(result) shouldBe NOT_FOUND
      }
    }
  }

  "Search" should {
    "work for simple phrase" in {
      val result = controller.search(1, "search")(request)
      status(result) shouldBe OK
      contentAsString(result) should include(posts(0).title)
      contentAsString(result) should include(posts(1).title)
    }

    "return not found when trying to access not existing page" in {
      val cases = Table("page", -1, 0, 3, 4)
      forAll(cases) { page =>
        val result = controller.search(page, "search")(request)
        status(result) shouldBe NOT_FOUND
      }
    }
  }
}
