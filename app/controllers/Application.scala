package controllers

import elements.Breadcrumb
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json.Json
import models.{TagUsage, Tag, Post, Db}
import security.Secured

import scala.concurrent.Future


class Application extends Controller with Secured {

  def index = Action { implicit request =>
    Ok(views.html.index(Db.query[Post].limit(5).order("date", true).fetch.toList))
  }

  def error = Action { implicit request =>
    Ok(views.html.error("Testowa wiadomość błędu"))
  }

  def listItems = Action { implicit request =>
    val posts = Db.query[Post].fetch()
    Ok(Json.toJson(posts))
  }

  def listTags = Action { implicit request =>
    Ok(Json.toJson(Db.query[Tag].fetch()))
  }

  def tagsUsage = Action { implicit request =>
    Ok(Json.toJson(Db.query[TagUsage].order("count", true).fetch()))
  }
}
