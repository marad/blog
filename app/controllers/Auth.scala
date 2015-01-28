package controllers

import models.Account
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

class Auth extends Controller {

  val loginForm: Form[Account] = Form {
    mapping(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText,
      "email" -> ignored("")
    )(Account.apply)(Account.unapply)
  }

  def viewLoginForm = Action { implicit request =>
    Ok(views.html.auth.login(loginForm))
  }

  def login = Action { implicit request =>
    loginForm bindFromRequest() fold ({ errors =>
      BadRequest(views.html.auth.login(errors))
    }, { creds =>
      if (creds.name == "morti" && creds.password == "ewqdsacxz") {
        Redirect(routes.Application.index()).withSession(
          Security.username -> creds.name
        )
      } else {
        Ok(views.html.auth.login(loginForm.withGlobalError("Błąd logowania")))
      }
      // TODO fetch from database
//      Db.query[Account]
//        .whereEqual("name", creds.name)
//        .whereEqual("password", creds.password)
//        .fetchOne() match {
//        case acc: Account =>
//          Redirect(routes.Application.index()).withCookies(
//            Cookie("user", "morti")
//          )
//        case _ =>
//          loginForm.globalErrors.map { _.message}
//          Ok(views.html.auth.login(loginForm.withGlobalError("Błąd logowania")))
//      }
    })
  }

  def logout = Action { implicit request =>
    Redirect(routes.Application.index()).withNewSession
  }

}
