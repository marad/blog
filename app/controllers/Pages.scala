package controllers

import play.api.mvc.{Action, Controller}

object Pages extends Controller {

  def programs = Action { implicit request =>
    Ok(views.html.pages.programs())
  }

  def about = Action { implicit request =>
    Ok(views.html.pages.about())
  }

}
