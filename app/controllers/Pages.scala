package controllers

import play.api.mvc.{Action, Controller}

object Pages extends Controller {

  def programs = Action {
    Ok(views.html.pages.programs())
  }

  def about = Action {
    Ok(views.html.pages.about())
  }

}
