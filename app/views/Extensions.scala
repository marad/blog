package views

import eu.henkelmann.actuarius.ActuariusTransformer
import org.joda.time.{ReadableInstant, DateTime}
import org.joda.time.format.DateTimeFormat
import play.api.Play
import play.api.mvc.{RequestHeader, Security, Request}
import play.twirl.api.Html

object Extensions {

  // JODA TIME WRAPPER
  class JodaTimeWrapper[T <: ReadableInstant](t:T) {
    def format(pattern: String): String = DateTimeFormat.forPattern(pattern).print(t)

    def formatDateTime(): String = {
      Play.current.configuration.getString("datetime.format") match {
        case Some(pattern) =>
          val fmt = DateTimeFormat.forPattern (pattern)
          fmt.print (t)
        case None => "Brak datetime.format w application.conf"
      }
    }
  }
  implicit def convertToJodaTimeWrapper[T <: ReadableInstant](t:T) = new JodaTimeWrapper(t)

  // STRING WRAPPER
  class StringWrapper[T <: String](t:T) {
    val markupTransformer = new ActuariusTransformer()
    def markupToHtml(): Html = Html.apply(markupTransformer.apply(t))
  }
  implicit def convertToStringWrapper[T <: String](t:String) = new StringWrapper(t)


  // AUTHORIZATION
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
