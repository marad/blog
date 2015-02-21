package controllers

import database.{Dao, DbTestData}
import models.Post
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.OneAppPerSuite
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.api.test.FakeRequest
import play.test.WithApplication

class PostsTest extends WordSpec with ControllerSpec with Matchers with MockitoSugar with OneAppPerSuite {

  val daoMock: Dao = mock[Dao]
  val posts = new Posts(daoMock)
  val publishedPost = Post.fromDbPostAndTags(DbTestData.firstPostData, Seq())
  val unpublishedPost = Post.fromDbPostAndTags(DbTestData.secondPostData, Seq())

  when(daoMock.findPost(1)) thenReturn Some(publishedPost)
  when(daoMock.findPost(2)) thenReturn Some(unpublishedPost)


  "View" should {
    "show existing and published posts" in {
      val request = FakeRequest("GET", routes.Posts.view(1).url)
      val result = posts.view(1)(request)

      contentAsString(result) should include(publishedPost.content)
    }

    "show error for not existing posts" in {
      val request = FakeRequest("GET", routes.Posts.view(5).url)
      val result = posts.view(5)(request)

      contentAsString(result) should include("Post not found")
    }

    "show error for not published posts" in {
      val request = FakeRequest("GET", routes.Posts.view(2).url)
      val result = posts.view(2)(request)

      contentAsString(result) should include("Post not found")
    }
  }

  "Insert" should {
    "push post to the database" in new WithApplication {
      when(daoMock.savePost(publishedPost)) thenReturn publishedPost.id.get
      val request = FakeRequest("POST", routes.Posts.insert().url)
        .withSession(Security.username -> "mori")
        .withFormUrlEncodedBody(posts.postForm.fill(publishedPost).data.toList:_*)
//        .withFormUrlEncodedBody(
//          "title" -> "tttt",
//          "short" -> "sssss",
//          "content" -> "ccccc",
//          "published" -> "1"
//        )

      val body = Enumerator("title=asd&short=qqq&content=zzzz&published=1".getBytes)
      val result = body.run(posts.insert(request))

//      val result = posts.insert(request).run

//      status(result) shouldBe 303
//      redirectLocation(result) shouldBe "/"
    }
  }
}

