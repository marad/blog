package controllers

import controllers.blog.Pager
import database.Dao
import models.Post
import play.api.libs.json.Json
import play.api.mvc._
import security.Secured

class PostManager(val dao: Dao, val pager: Pager)
  extends Controller
  with Secured {

  case class PostView(id: Long, title: String, published: Boolean)
  object PostView {
    implicit val postFormat = Json.format[PostView]
    def fromPost(p:Post) = PostView(p.id.get, p.content, p.published)
  }

  def get = loggedIn { implicit request =>
    Ok(views.html.manager())
  }

  def getPageCount = loggedIn { implicit request =>
    val pages = pager.countMaxPages(dao.postCount())
    Ok(Json.parse("{ \"pageCount\": "+ pages +" }"))
  }

  def getPage(page: Int) = loggedIn { implicit request =>
    val maxPages = pager.countMaxPages(dao.postCount())
    if (page >= 1 && page <= maxPages) {
      val posts = dao.listPosts(pager.postsPerPage * (page-1), pager.postsPerPage, publishedOnly = false)
      Ok(Json.toJson(posts.map(PostView.fromPost)))
    } else {
      NotFound("Page not found.")
    }
  }
}
