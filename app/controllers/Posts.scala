package controllers

import controllers.blog.{Pager, PostCalendar, PostCrud, PostListings}
import database.Dao
import play.api.mvc._
import security.Secured

class Posts(val dao: Dao, val pager: Pager)
  extends Controller
  with Secured
  with PostCrud
  with PostListings
  with PostCalendar

