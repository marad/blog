package controllers

import database.slick.Db
import models._
import config.Config
import security.Secured
import elements.Breadcrumb
import viewmodel.CalendarEvent

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json

import sorm.Persisted
import sorm.persisted.Persisted
import sorm.Querier



class Posts(val db: Db) extends Controller with Secured {

  import utils.MessageType._

  val tagForm: Form[Tag] = Form {
    mapping(
//      "id" -> ignored(None:Option[Long]),
      "name" -> nonEmptyText
    )(Tag.apply)(Tag.unapply)
  }

  val postForm: Form[Post] = Form {
    mapping(
//      "id" -> ignored(None:Option[Long]),
      "title" -> nonEmptyText,
      "short" -> nonEmptyText,
      "content" -> nonEmptyText,
      "date" -> ignored(new DateTime()),
      "updated" -> ignored(new DateTime()),
      "tags" -> text.transform( { tags: String =>
        (tags split """\s*,\s*""" map { new Tag(_) }).toSeq
      }, { tags: Seq[Tag] =>
        tags.map({ _.name }).reduce{ (l: String, r:String) => l + ", " + r }
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
      BadRequest(views.html.editor(errors))
    },
      { post =>
        Db.transaction {
          val savedPost = Db.save(post.copy(tags = post.tags.map(Db.saveByUniqueKeys(_))))
          Redirect(routes.Posts.view(savedPost.id))
        }
      })
  }

  def save(id: Long) = loggedIn { implicit request =>
    postForm bindFromRequest() fold ({ errors =>
      BadRequest(views.html.editor(errors))
    }, { post =>
      Db.transaction {
        val originalPost = Db.fetchById[Post](id)
        val updatedPost = Persisted(post, id)
        val tags = updatedPost.tags.map(Db.saveByUniqueKeys(_))
        Db.save(updatedPost.copy(tags = tags, date = originalPost.date, updated = new DateTime()))
        Redirect(routes.Posts.view(id))
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

  def delete(id: Long) = loggedIn { implicit request =>
    Db.query[Post].whereEqual("id", id).fetchOne match {
      case Some(post) => 
        Db.delete[Post](post)
        Redirect(routes.Application.index())
          .flashing( SuccessMessage -> s"Usunięto post $id")
      case _ => NotFound(views.html.error("Nie odnaleziono posta"))
    }
  }

  def countMaxPages(query: Querier[Post]) =
    Math.ceil(query.count().toDouble / Config.postsPerPage.toDouble).toInt - 1

  def listIndex = list(0)
  def list(page: Int) = Action { implicit request =>
    val postsPerPage = Config.postsPerPage
    val maxPages = countMaxPages(Db.query[Post])

    if (page < 0 || page > maxPages) {
      Redirect(routes.Posts.listIndex())
        .flashing( ErrorMessage -> "Niepoprawna strona")
    } else {
      val posts = Db.query[Post]
        .order("date", true)
        .offset(postsPerPage * page)
        .limit(postsPerPage)
        .fetch
      Ok(views.html.list(page, maxPages, posts))
    }
  }


  def search(page: Int = 0, phrase: String) = Action { implicit request =>

    import sorm.Dsl._

    val criteria = phrase.split("\\s+")
    val query: Querier[Post] = criteria.foldLeft(Db.query[Post]) { (qr: Querier[Post], s: String) =>
      qr.where(
        ( ("title"   like s"%$s%" ) or
          ("short"   like s"%$s%" ) or
          ("content" like s"%$s%" ) )
      )

    }

    val maxPages = countMaxPages(query)

    val posts = query
      .order("date", true)
      .offset(Config.postsPerPage * page)
      .limit(Config.postsPerPage)
      .fetch
    Ok(views.html.list(page, maxPages, posts))
  }

  def calendar = Action { implicit request =>
    Ok(views.html.calendar())
  }

  def calendarData(startOpt: Option[String], endOpt: Option[String]) = Action { implicit request =>
    (startOpt, endOpt) match {
      case (Some(start), Some(end)) =>
        val format = DateTimeFormat.forPattern("yyyy-MM-dd")
        val startDate = LocalDate.parse(start, format)
        val endDate = LocalDate.parse(end, format)
        val posts = Db.query[Post]
          .whereLargerOrEqual("date", startDate)
          .whereSmallerOrEqual("date", endDate)
          .fetch
        val events = posts.map(CalendarEvent.fromPost)

        Ok(Json.toJson(events))
      case _ => Ok(Json.parse("[]"))
    }
  }

}

