package controllers

import elements.Breadcrumb
import org.joda.time.DateTime
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import security.Secured

object Posts extends Controller with Secured {

  val tagForm: Form[Tag] = Form {
    mapping(
      "name" -> nonEmptyText
    )(Tag.apply)(Tag.unapply)
  }

  val postForm: Form[Post] = Form {
    mapping(
      "title" -> nonEmptyText,
      "short" -> nonEmptyText,
      "content" -> nonEmptyText,
      "date" -> ignored(new DateTime()),
      "tags" -> text.transform( { tags: String =>
        (tags split """\s*,\s*""" map { new Tag(_) }).toSeq
      }, { tags: Seq[Tag] =>
        tags map { _.name } reduce { (a:String, b:String) => a + b }
      })
    )(Post.apply)(Post.unapply)
  }

  def view(id: Long) = Action { implicit request =>

    val breadcrumbs = List(
      new Breadcrumb("Strona Główna", "/"),
      new Breadcrumb("Post")
    )

    Db.query[Post].whereEqual("id", id).fetchOne() match {
      case Some(p) => Ok(views.html.post(p, breadcrumbs))
      case _ => NotFound(views.html.error("Nie odnaleziono posta"))
    }
  }

  def insert = loggedIn { implicit request =>
    postForm bindFromRequest() fold ({ errors =>
      println(errors)
      BadRequest(views.html.editor(errors))
    },
      { post =>
        Db.transaction {
          val savedPost = Db.save(post.copy(tags = post.tags.map(Db.saveByUniqueKeys(_))))
          Redirect(routes.Posts.view(savedPost.id))
        }
      })
  }

  def create = loggedIn { implicit request =>
    Ok(views.html.editor(postForm))
  }

  def edit(id: Long) = loggedIn { implicit request =>
    Db.query[Post].whereEqual("id", id).fetchOne match {
      case Some(post) => Ok(views.html.editor(postForm.fill(post)))
      case _ => NotFound(views.html.error("Nie odnaleziono posta"))
    }
  }

}
