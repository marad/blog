package controllers

import database.{Dao, DbTestData}
import models.Post
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.mockito.verification.VerificationMode
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.OneAppPerSuite
import play.api.mvc._
import play.api.test.{EssentialActionCaller, FakeRequest, Writeables}

class PostsTest extends WordSpec with ControllerSpec with Matchers with MockitoSugar
with OneAppPerSuite with EssentialActionCaller with Writeables
with TableDrivenPropertyChecks {

  val daoMock: Dao = mock[Dao]
  val posts = new Posts(daoMock)
  val publishedPost = Post.fromDbPostAndTags(DbTestData.firstPostData, Seq())
  val unpublishedPost = Post.fromDbPostAndTags(DbTestData.secondPostData, Seq())

  when(daoMock.findPost(1)) thenReturn Some(publishedPost)
  when(daoMock.findPost(2)) thenReturn Some(unpublishedPost)
  when(daoMock.findPost(5)) thenReturn None

}

