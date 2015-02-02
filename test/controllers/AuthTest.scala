package controllers

import database.Dao
import models.Account
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FlatSpec, Matchers}
import play.api.mvc.{AnyContent, Request}

class AuthTest extends FlatSpec with Matchers with MockitoSugar{

  val daoMock: Dao = mock[Dao]
  val requestMock: Request[AnyContent] = mock[Request[AnyContent]]

  val auth = new Auth(daoMock)

  when(daoMock.findAccount("existing")) thenReturn Some(AuthTest.existingAccount)

//  "Auth Controller" should "view login form" in {
//    callAction(auth.viewLoginForm)
//    val futureResult: Future[Result] = auth.viewLoginForm.apply(requestMock)
//
//    futureResult.onComplete {
//      case Success(result) =>
//        result.
//      case Failure(reason) => fail(reason)
//    }
//  }
}

object AuthTest {
  val existingAccount = Account(Some(1l), "existing", "pass")
}
