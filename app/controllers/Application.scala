package controllers

import database.Dao
import play.api.libs.json.Json
import play.api.mvc._
import security.Secured


class Application(dao: Dao) extends Controller with Secured {

  def index = Action { implicit request =>

    Ok(views.html.index(dao.listPosts(0, 5)))
  }

  def error = Action { implicit request =>
    Ok(views.html.error("Testowa wiadomość błędu"))
  }

  def listItems = Action { implicit request =>
    val posts = dao.listPostsOnFirstPage()
    Ok(Json.toJson(posts))
  }
}
