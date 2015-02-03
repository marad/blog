package controllers

import database.Dao
import models.Account
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.Matchers
import play.api.mvc.{Result, AnyContent, Request}
import play.api.test._
import org.scalatest.PartialFunctionValues._

import scala.concurrent.Future

class AuthTest extends ControllerSpec with Matchers with MockitoSugar {

  val daoMock: Dao = mock[Dao]
  val requestMock: Request[AnyContent] = mock[Request[AnyContent]]

  val auth = new Auth(daoMock)

  when(daoMock.findAccount("existing")) thenReturn Some(AuthTest.existingAccount)
  when(daoMock.findAccount("invalid")) thenReturn None

  "Auth Controller" should "redirect to index on successful login and set username in session" in {
    val validRequest = FakeRequest("POST", "/ignored")
      .withFormUrlEncodedBody(
        "username" -> "existing",
        "password" -> "pass"
      )
    val result: Future[Result] = auth.login(validRequest)

    status(result) shouldBe SEE_OTHER
    redirectLocation(result) shouldBe Some("/")
    session(result).data.valueAt("username") shouldBe "existing"
  }

  it should "show error on invalid credentials" in {
    val request = FakeRequest("POST", "/ignored")
      .withFormUrlEncodedBody(
        "username" -> "invalid",
        "password" -> "password"
      )
    val result = auth.login(request)

    status(result) shouldBe BAD_REQUEST
    contentAsString(result) contains "Login incorrect"
  }
}

object AuthTest {
  val existingAccount = Account(Some(1l), "existing", "pass").hashPassword
}
