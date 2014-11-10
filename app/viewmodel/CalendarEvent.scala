package viewmodel

import models.Post
import controllers.routes

import sorm.Persisted
import play.api.libs.json.Json
import org.joda.time.LocalDate

case class CalendarEvent(id: Long, title: String, start: LocalDate, url: String)

object CalendarEvent {
  def fromPost(post: Post with Persisted): CalendarEvent = 
    new CalendarEvent(
      post.id,
      post.title,
      post.date.toLocalDate(),
      routes.Posts.view(post.id).toString
    )


  implicit val calendarEventFormat = Json.format[CalendarEvent]
}
