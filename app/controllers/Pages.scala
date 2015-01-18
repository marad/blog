package controllers

import play.api.mvc.{Action, Controller}

class Pages extends Controller {

  def programs = Action { implicit request =>
    Ok(views.html.pages.programs())
  }

  def about = Action { implicit request =>
    Ok(views.html.pages.about())
  }

  def music = Action { implicit request =>
    Ok(views.html.pages.music())
  }

  def games = Action { implicit request =>
    Ok(views.html.pages.games())
  }
}
