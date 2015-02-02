package controllers

import database.Dao
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import models.Account

class Auth(dao: Dao) extends Controller {

  val loginForm: Form[Account] = Form {
    mapping(
      "id" -> ignored[Option[Long]](None),
      "username" -> nonEmptyText,
      "password" -> nonEmptyText
    )(Account.apply)(Account.unapply)
  }

  def viewLoginForm = Action { implicit request =>
    Ok(views.html.auth.login(loginForm))
  }

  def login = Action { implicit request =>
    loginForm bindFromRequest() fold ({ errors =>
      BadRequest(views.html.auth.login(errors))
    }, { creds =>
      val success = dao.findAccount(creds.username) match {
        case Some(acc@Account(_, _)) => acc.checkPassword(creds.password)
        case None => false
      }

      if (success) {
        Redirect(routes.Application.index()).withSession(
          Security.username -> creds.username
        )
      } else {
        Ok(views.html.auth.login(loginForm.withGlobalError("Błąd logowania")))
      }
    })
  }

  def logout = Action { implicit request =>
    Redirect(routes.Application.index()).withNewSession
  }

}
