package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import security.Secured
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import models._

object Preview extends Controller with Secured {

  private var preview: Option[Post] = None

  val postForm: Form[Post] = Form {
    mapping(
      "id" -> ignored(None:Option[Long]),
      "title" -> text,
      "short"-> text,
      "content" -> text,
      "date" -> ignored(new DateTime()),
      "updated" -> ignored(new DateTime()),
      "tags" -> ignored(Seq[Tag]())
    )(Post.apply)(Post.unapply)
  }

  def update = loggedIn { implicit request =>
    postForm bindFromRequest() fold ({ errors =>
      BadRequest("Invalid request")
    }, { post =>
      preview = Some(post)
      Ok("preview set")
    })
  }

  def view = loggedIn { implicit request =>
    preview match {
      case Some(post) => Ok(views.html.post(post))
      case _ => NotFound(views.html.error("No post to set"))
    }
  }
}
