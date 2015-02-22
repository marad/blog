package controllers.blog

import controllers.routes
import database.Dao
import play.api.mvc._

trait PostListings { this : Controller =>
  val dao: Dao
  def pager: Pager

  def listIndex = list(1)

  def list(page: Int) = Action { implicit request =>
    val maxPages = pager.countMaxPages(dao.postCount())

    if (isValidPage(page, maxPages)) {
      val posts = dao.listPosts(pager.postsPerPage * (page-1), pager.postsPerPage)
      Ok(views.html.list(page, maxPages, posts, listRoute))
    } else {
      NotFound(views.html.error(s"Page $page does not exist!"))
    }
  }

  def search(page: Int, phrase: String) = Action { implicit request =>
    val criteria = phrase.split("\\s+")
    val maxPages = pager.countMaxPages(dao.countAllSearchResults(criteria))

    if (isValidPage(page, maxPages)) {
      val posts = dao.searchWithPhrase(pager.postsPerPage * (page-1), pager.postsPerPage, criteria)
      Ok(views.html.list(page, maxPages, posts, searchRoute(phrase)))
    } else {
      NotFound(views.html.error(s"Page $page does not exist for search phrase: $phrase"))
    }
  }

  private def isValidPage(page:Int, maxPages:Int) = page > 0 && page <= maxPages
  private def listRoute(page: Int): String = routes.Posts.list(page).url
  private def searchRoute(phrase: String)(page: Int): String = routes.Posts.search(page, phrase).url
}
