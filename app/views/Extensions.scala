package views

import org.joda.time.{ReadableInstant, DateTime}
import org.joda.time.format.DateTimeFormat
import play.api.mvc.{RequestHeader, Security, Request}
import play.twirl.api.Html

object Extensions {
  class JodaTimeWrapper[T <: ReadableInstant](t:T) {
    def format(pattern: String): String = {
      val fmt = DateTimeFormat.forPattern(pattern)
      fmt.print(t)
    }
  }
  implicit def convertToJodaTimeWrapper[T <: ReadableInstant](t:T) = new JodaTimeWrapper(t)

  def loggedIn(content: Html)(implicit request: RequestHeader): Option[Html] =
    request.session.get(Security.username) match {
      case Some(_) => Some(content)
      case None => None
    }

  def notLoggedIn(content: Html)(implicit request: RequestHeader): Option[Html] =
    request.session.get(Security.username) match {
      case Some(_) => None
      case None => Some(content)
    }

  def loggedUserName(implicit request: RequestHeader): String =
    request.session.get(Security.username) match {
      case Some(username) => username
      case None => ""
    }
}
