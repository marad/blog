package config

import database.slick.{Dao, Db}
import controllers.{Feed, Application, Posts}


object Config extends play.api.GlobalSettings {

  lazy val dbUrl = "jdbc:h2:mem:test"
//  lazy val dbUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
  lazy val dbDriverClass = "org.h2.Driver"
  lazy val dbDriver = scala.slick.driver.H2Driver.simple

  val database = new Db
  val dao = new Dao(database)

  private val postsController = new Posts(dao)
  private val applicationController = new Application(dao)
  private val feedController = new Feed(dao)

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    if (controllerClass == classOf[Posts]) postsController.asInstanceOf[A]
    else if (controllerClass == classOf[Application]) applicationController.asInstanceOf[A]
    else if (controllerClass == classOf[Feed]) feedController.asInstanceOf[A]
    else super.getControllerInstance(controllerClass)
  }

  val postsPerPage : Int =
    configuration.getInt("pages.list.postsPerPage") match {
      case Some(value) => value
      case None => 10
    }
}
