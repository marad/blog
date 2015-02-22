package controllers.blog

import controllers.{routes, ControllerSpec}
import database.{DbTestData, Dao}
import models.Post
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.OneAppPerSuite
import play.api.mvc.{Controller, Security}
import play.api.test.{FakeRequest, Writeables, EssentialActionCaller}
import org.mockito.Mockito._
import org.mockito.Matchers._
import security.Secured
import utils.MessageType

class PostCrudTest extends WordSpec with ControllerSpec with Matchers with MockitoSugar
with OneAppPerSuite with EssentialActionCaller with Writeables
with TableDrivenPropertyChecks {

  class TestController(val dao: Dao) extends Controller with Secured with PostCrud

  val daoMock: Dao = mock[Dao]
  val posts: PostCrud = new TestController(daoMock)
  val publishedPost = Post.fromDbPostAndTags(DbTestData.firstPostData, Seq())
  val unpublishedPost = Post.fromDbPostAndTags(DbTestData.secondPostData, Seq())

  when(daoMock.findPost(1)) thenReturn Some(publishedPost)
  when(daoMock.findPost(2)) thenReturn Some(unpublishedPost)
  when(daoMock.findPost(5)) thenReturn None

  def req = new {
    val post = FakeRequest("POST", "/ignored")
    val get = FakeRequest("GET", "/ignored")
    val authorizedPost = FakeRequest("POST", "/ignored")
      .withSession(Security.username -> "mori")
    val authorizedGet = FakeRequest("GET", "/ignored")
      .withSession(Security.username -> "mori")
    def authorizedPostWithBody(p:Post) = authorizedPost
      .withFormUrlEncodedBody(posts.postForm.fill(p).data.toList:_*)
  }

  "View" should {
    val actions = Table(
      ("action", "expectedContent", "desc"),
      (posts.view(1), publishedPost.content, "show existing and published posts"),
      (posts.view(5), "Post not found", "show error for not existing posts"),
      (posts.view(2), "Post not found", "show error for not published posts")
    )

    forAll(actions) { (action, expectedContent, desc) =>
      desc in {
        val result = action(req.get)
        contentAsString(result) should include(expectedContent)
      }
    }
  }

  "Saving post to database" should {
    when(daoMock.savePost(any())) thenReturn 1
    val cases = Table(
      ("action", "desc"),
      (posts.insert, "when inserting"),
      (posts.save(1), "when overwriting")
    )

    forAll(cases) { (action, desc) =>
      s"redirect to post page on success $desc" in {
        val request = req.authorizedPostWithBody(publishedPost)
        val result = call(action, request)
        status(result) shouldBe SEE_OTHER
        redirectLocation(result) shouldBe Some(routes.Posts.view(1).url)
      }
    }

    forAll(cases) { (action, desc) =>
      s"respond with BadRequest if required fields are missing $desc" in {
        val result = call(action, req.authorizedPost)
        status(result) shouldBe BAD_REQUEST
      }
    }
  }

  "Create" should {
    "show page for creating post" in {
      val result = call(posts.create, req.authorizedGet)

      status(result) shouldBe OK
      contentAsString(result) should include("Post Editor")
    }
  }

  "Edit" should {
    "show page for editing" in {
      val result = call(posts.edit(1), req.authorizedGet)
      status(result) shouldBe OK
      contentAsString(result) should include("Post Editor")
    }

    "respond with NotFound if post to edit does not exist" in {
      val result = call(posts.edit(5), req.authorizedGet)
      status(result) shouldBe NOT_FOUND
    }
  }

  "Delete" should {
    "remove existing post from database" in {
      val result = call(posts.delete(1), req.authorizedGet)
      status(result) shouldBe SEE_OTHER
      redirectLocation(result) shouldBe Some("/")
      verify(daoMock).deletePost(1)
    }

    "return NotFound for not existing post" in {
      val result = call(posts.delete(5), req.authorizedGet)
      status(result) shouldBe NOT_FOUND
      contentAsString(result) should include("Post not found")
      verify(daoMock, never()).deletePost(5)
    }
  }

  "Security" should {
    val cases = Table(
      ("action", "desc"),
      (posts.edit(1), "editing"),
      (posts.insert, "inserting"),
      (posts.save(1), "saving"),
      (posts.create, "creating"),
      (posts.delete(1), "deleting")
    )

    forAll(cases) { (action, desc) =>
      s"require authorization when $desc" in {
        val result = call(action, req.get)
        status(result) shouldBe SEE_OTHER
        flash(result).get(MessageType.ErrorMessage) shouldBe Some("You cannot access this resource.")
      }
    }

    "not require authorization when viewing" in {
      val result = call(posts.view(1), req.get)
      status(result) shouldBe OK
    }
  }
}
