package controllers

import config.Config
import database.Dao
import elements.Breadcrumb
import models._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.data.Forms._
import play.api.data._
import play.api.libs.json.Json
import play.api.mvc._
import security.Secured
import viewmodel.CalendarEvent

class Posts(val dao: Dao) extends Controller with Secured {

  import utils.MessageType._

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
      "tags" -> text.transform( { tags: String =>
        (tags split """\s*,\s*""" map { new Tag(None, _) }).toSeq
      }, { tags: Seq[Tag] =>
        if (tags.isEmpty) ""
        else tags.map({ _.name }).reduce{ (l: String, r:String) => l + ", " + r }
      })
    )(Post.apply)(Post.unapply)
  }

  def view(id: Long) = Action { implicit request =>

    val breadcrumbs = List(
      new Breadcrumb("Strona Główna", "/"),
      new Breadcrumb("Post")
    )

    dao.readPost(id) match {
      case Some(p) => Ok(views.html.post(p, breadcrumbs))
      case _ => NotFound(views.html.error("Nie odnaleziono posta"))
    }
  }

  def insert = loggedIn { implicit request =>
    postForm bindFromRequest() fold ({ errors =>
      BadRequest(views.html.editor(errors))
    },
      { post =>
        val id = dao.writePost(post)
        Redirect(routes.Posts.view(id))
      })
  }

  def save(id: Long) = loggedIn { implicit request =>
    postForm bindFromRequest() fold ({ errors =>
      BadRequest(views.html.editor(errors))
    }, { post =>
      dao.writePost(post.copy(id = Some(id)))
      Redirect(routes.Posts.view(id))
    })
  }

  def create = loggedIn { implicit request =>
    Ok(views.html.editor(postForm))
  }

  def edit(id: Long) = loggedIn { implicit request =>
    dao.readPost(id) match {
      case Some(post) => Ok(views.html.editor(postForm.fill(post)))
      case _ => NotFound(views.html.error("Nie odnaleziono posta"))
    }
  }

  def delete(id: Long) = loggedIn { implicit request =>
    dao.deletePost(id)
    Redirect(routes.Application.index())
      .flashing( SuccessMessage -> s"Usunięto post $id")
    // TODO case _ => NotFound(views.html.error("Nie odnaleziono posta"))
  }

  def countMaxPages(postCount: Int) =
    Math.ceil(postCount.toDouble / Config.postsPerPage.toDouble).toInt - 1

  def listIndex = list(0)
  def list(page: Int) = Action { implicit request =>
    val postsPerPage = Config.postsPerPage
    val maxPages = countMaxPages(dao.postCount())

    if (page < 0 || page > maxPages) {
      Redirect(routes.Posts.listIndex())
        .flashing( ErrorMessage -> "Niepoprawna strona")
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

