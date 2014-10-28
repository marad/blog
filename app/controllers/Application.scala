package controllers

import elements.Breadcrumb
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.libs.json.Json
import models.{TagUsage, Tag, Post, Db}

import scala.concurrent.Future


object Application extends Controller {

  System.setProperty("org.slf4j.simpleLogger.log.sorm", "debug")

  def index = Action {
    Ok(views.html.index(Db.query[Post].limit(5).order("date", true).fetch.toList))
  }

  def error = Action {
    Ok(views.html.error("Testowa wiadomość błędu"))
  }

  def listItems = Action {
    val posts = Db.query[Post].fetch()
    Ok(Json.toJson(posts))
  }

  def listTags = Action {
    Ok(Json.toJson(Db.query[Tag].fetch()))
  }

  def tagsUsage = Action {
    Ok(Json.toJson(Db.query[TagUsage].order("count", true).fetch()))
  }

  def test(f: Request[AnyContent] => Future[Result]): Unit = {

  }
}
