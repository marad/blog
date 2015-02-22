package controllers

import config.Config
import database.Dao
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.Json
import play.api.mvc._
import security.Secured
import viewmodel.CalendarEvent

class Posts(val dao: Dao) extends Controller with Secured
with PostCrud {

  import utils.MessageType._

  def countMaxPages(postCount: Int) =
    Math.ceil(postCount.toDouble / Config.postsPerPage.toDouble).toInt - 1

  def listIndex = list(0)
  def list(page: Int) = Action { implicit request =>
    val postsPerPage = Config.postsPerPage
    val maxPages = countMaxPages(dao.postCount())

    if (page < 0 || page > maxPages) {
      Redirect(routes.Posts.listIndex())
        .flashing( ErrorMessage -> "Invalid page")
    } else {
      val posts = dao.listPosts(postsPerPage * page, postsPerPage)
      Ok(views.html.list(page, maxPages, posts))
    }
  }

  def search(page: Int = 0, phrase: String) = Action { implicit request =>
    val criteria = phrase.split("\\s+")
    val maxPages = countMaxPages(dao.countAllSearchResults(criteria))
    val posts = dao.searchWithPhrase(Config.postsPerPage * page, Config.postsPerPage, criteria)
    Ok(views.html.list(page, maxPages, posts))
  }

  def calendar = Action { implicit request =>
    Ok(views.html.calendar())
  }

  def calendarData(startOpt: Option[String], endOpt: Option[String]) = Action { implicit request =>
    (startOpt, endOpt) match {
      case (Some(start), Some(end)) =>
        val format = DateTimeFormat.forPattern("yyyy-MM-dd")
        val startDate = DateTime.parse(start, format)
        val endDate = DateTime.parse(end, format)
        val posts = dao.listsPostsForPeriod(startDate, endDate)
        val events = posts.map(CalendarEvent.fromPost)

        Ok(Json.toJson(events))
      case _ => Ok(Json.parse("[]"))
    }
  }

}

