package config

import database.slick.Db
import controllers.Posts


object Config extends play.api.GlobalSettings {

  lazy val dbUrl = "jdbc:h2:mem:test"
//  lazy val dbUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
  lazy val dbDriverClass = "org.h2.Driver"
  lazy val dbDriver = scala.slick.driver.H2Driver.simple

  val database = new Db

  val postsController = new Posts(database)

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    if (controllerClass == classOf[Posts]) postsController.asInstanceOf[A]
    else super.getControllerInstance(controllerClass)
  }

  val postsPerPage : Int =
    configuration.getInt("pages.list.postsPerPage") match {
      case Some(value) => value
      case None => 10
    }
}
