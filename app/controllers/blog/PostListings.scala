package controllers.blog

import config.Config
import controllers.routes
import database.Dao
import play.api.mvc._
import utils.MessageType

trait PostListings { this : Controller =>
  val dao: Dao

  def countMaxPages(postCount: Int) =
    Math.ceil(postCount.toDouble / Config.postsPerPage.toDouble).toInt - 1

  def listIndex = list(0)
  def list(page: Int) = Action { implicit request =>
    val postsPerPage = Config.postsPerPage
    val maxPages = countMaxPages(dao.postCount())

    if (page < 0 || page > maxPages) {
      Redirect(routes.Posts.listIndex())
        .flashing( MessageType.ErrorMessage -> "Invalid page")
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
}
