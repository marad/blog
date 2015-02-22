package viewmodel

import controllers.routes
import org.joda.time.LocalDate
import play.api.libs.json.Json
import models.Post

case class CalendarEvent(id: Long, title: String, start: LocalDate, url: String)

object CalendarEvent {
  def fromPost(post: Post): CalendarEvent =
    new CalendarEvent(
      post.id.get,
      post.title,
      post.created.toLocalDate,
      routes.PostCrud.view(post.id.get).toString()
    )


  implicit val calendarEventFormat = Json.format[CalendarEvent]
}
