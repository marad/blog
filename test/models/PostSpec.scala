package models

import org.specs2.mutable_
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

import joda.time.DateTime

import model.Post
import model.Tag

@RunWith(classOf[JUnitRunner])
class PostSpec extends Specification {
  "Post" should {
    "copy post object" in {
      val p = Post("some title", "short", "content", new DateTime(), new DateTime(), tags)
      val copy = p.copyPost(p)
      copy must equalTo(p)
    }
  }
}
