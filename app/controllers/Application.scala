package controllers

import database.Dao
import org.markdown4j.Markdown4jProcessor
import play.api.libs.json.Json
import play.api.mvc._
import security.Secured


class Application(dao: Dao) extends Controller with Secured {

  val markdown4jProcessor = new Markdown4jProcessor()

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

  def renderMarkdown = Action(parse.tolerantText) { implicit request =>
    Ok(markdown4jProcessor.process(request.body))
//    request.body.get("markdown") match {
//      case Some(markdown) =>
//        if (markdown.size > 0) Ok(markdown4jProcessor.process(markdown(0)))
//        else BadRequest("Invalid input")
//      case None => BadRequest("Missing input")
//    }
  }
}
