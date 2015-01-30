package controllers

import database.DatabaseTest
import org.scalatest.{Matchers, FlatSpec}
import org.scalatestplus.play.OneAppPerSuite
import play.api.test._
import play.api.test.Helpers._

class ApplicationSpec extends FlatSpec with Matchers with DatabaseTest with OneAppPerSuite {

  "Applicaton" should "send 404 on a bad request" in withDatabase {
    route(FakeRequest(GET, "/boum")) shouldBe None
  }

  it should "render the index page" in withDatabase {
    val home = route(FakeRequest(GET, "/")).get

    status(home) shouldBe OK
    contentType(home) shouldBe Some("text/html")
    contentAsString(home) contains "O mnie"
  }
}
