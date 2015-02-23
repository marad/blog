package controllers

import _root_.play.api.mvc.Security
import _root_.play.api.test.{EssentialActionCaller, Writeables, _}
import controllers.blog.Pager
import database.Dao
import org.scalatest.FreeSpec
import org.scalatest.prop.TableDrivenPropertyChecks
import utils.MessageType

class PostManagerTest extends FreeSpec with ControllerSpec with TableDrivenPropertyChecks
with EssentialActionCaller with Writeables {
  val dao = mock[Dao]
  val controller = new PostManager(dao, new Pager(5))

  def req = new {
    val unauthorizedGet = FakeRequest("GET", "/ignored")
    val post = FakeRequest("POST", "/ignored")
      .withSession(Security.username -> "mori")
    val get = FakeRequest("GET", "/ignored")
      .withSession(Security.username -> "mori")
  }

  new WithApplication {
    "Manager actions should be for authorized users only:" - {
      val cases = Table(
        ("action", "desc"),
        (controller.get, "get"),
        (controller.getPage(1), "getPage")
      )

      forAll(cases) { (action, desc) =>
        s"> $desc" - {
          val result = call(action, req.unauthorizedGet)
          status(result) shouldBe SEE_OTHER
          flash(result).get(MessageType.ErrorMessage) shouldBe Some("You cannot access this resource.")
        }
      }
    }
  }
}
