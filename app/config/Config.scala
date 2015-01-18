package config

import database.slick.Db
import controllers.Posts
import play.api.Play
import scala.slick.jdbc.JdbcBackend.Database


object Config extends play.api.GlobalSettings {

  val dbUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
  val dbDriverClass = "org.h2.Driver"
  val dbDriver = scala.slick.driver.H2Driver.simple

  val database = new Db

  val postsController = new Posts(database)

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    if (controllerClass == classOf[Posts]) postsController.asInstanceOf[A]
    else super.getControllerInstance(controllerClass)
  }

  def postsPerPage : Int =
    configuration.getInt("pages.list.postsPerPage") match {
      case Some(value) => value
      case None => 10
    }

//    Play.current.configuration.getString("pages.list.postsPerPage") match {
//      case Some(value) => value.toInt
//      case None => 10
//    }
}
