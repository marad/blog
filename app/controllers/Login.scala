package controllers

import play.api.mvc.{Cookie, Action, Controller}

object Login extends Controller {

  def viewLoginForm = Action {
    Ok("")
  }

  def login = Action {
    Ok("").withCookies(
      Cookie("user", "morti")
    )
  }

  def logout = Action { implicit request =>
    println(request)
    Ok("")
  }

}
