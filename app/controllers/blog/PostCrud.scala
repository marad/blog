package controllers.blog

import controllers.routes
import database.Dao
import models.{Post, Tag}
import org.joda.time.DateTime
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import security.Secured
import utils.MessageType

trait PostCrud { this : Controller with Secured =>
  val dao: Dao

  val tagForm: Form[Tag] = Form {
    mapping(
      "id" -> ignored(None:Option[Long]),
      "name" -> nonEmptyText
    )(Tag.apply)(Tag.unapply)
  }

  val postForm: Form[Post] = Form {
    mapping(
      "id" -> ignored(None:Option[Long]),
      "title" -> nonEmptyText,
      "short" -> nonEmptyText,
      "content" -> nonEmptyText,
      "date" -> ignored(new DateTime()),
      "updated" -> ignored(new DateTime()),
      "published" -> boolean,
      "tags" -> text.transform( { tags: String =>
        (tags split """\s*,\s*""" map { new Tag(None, _) }).toSeq
      }, { tags: Seq[Tag] =>
        if (tags.isEmpty) ""
        else tags.map({ _.name }).reduce{ (l: String, r:String) => l + ", " + r }
      })
    )(Post.apply)(Post.unapply)
  }

  def view(id: Long) = Action { implicit request =>
    dao.findPost(id) match {
      case Some(p) => Ok(views.html.post(p))
        if (p.published || isLoggedIn(request)) Ok(views.html.post(p))
        else NotFound(views.html.error("Post not found"))
      case _ => NotFound(views.html.error("Post not found"))
    }
  }

  def writePostToDatabase(givenId: Option[Long]) = { implicit request: Request[AnyContent] =>
    postForm bindFromRequest() fold( { errors =>
      BadRequest(views.html.editor(errors))
    }, { post =>
      val id = dao.savePost(post.copy(id = givenId))
      Redirect(routes.Posts.view(id))
    })
  }

  def insert = loggedIn(writePostToDatabase(None))
  def save(id: Long) = loggedIn(writePostToDatabase(Some(id)))

  def create = loggedIn { implicit request =>
    Ok(views.html.editor(postForm))
  }

  def edit(id: Long) = loggedIn { implicit request =>
    dao.findPost(id) match {
      case Some(post) => Ok(views.html.editor(postForm.fill(post)))
      case _ => NotFound(views.html.error("Post not found"))
    }
  }

  def delete(id: Long) = loggedIn { implicit request =>
    dao.findPost(id) match {
      case Some(p) =>
        dao.deletePost(id)
        Redirect(routes.Application.index())
          .flashing( MessageType.SuccessMessage -> s"Post ${p.title} has been deleted")
      case None =>
        NotFound(views.html.error("Post not found"))
    }
  }
}
