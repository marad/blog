package controllers.blog

import database.Dao
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.json.Json
import play.api.mvc._
import viewmodel.CalendarEvent

trait PostCalendar { this : Controller =>
  val dao: Dao

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
