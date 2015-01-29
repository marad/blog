package config

import database.slick.{Dao, Db}
import controllers.{Feed, Application, Posts}


object Config extends play.api.GlobalSettings {

  sealed abstract class Environment
  case object Production extends Environment
  case object Development extends Environment
  case object Test extends Environment

  lazy val environment: Environment = {
    println("ENVIRONMENT: " + System.getProperty("env"))
    System.getProperty("env") match {
      case "PROD" =>
        println("I WORK IN PRODUCTION MODE")
        Production
      case "TEST" =>
        println("I WORK IN TEST MODE")
        Test
      case _ =>
        println("I WORK IN DEVELOPMENT MODE")
        Development
    }
  }

  lazy val dbUrl = environment match {
    case Production => "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    case Development => "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
    case Test => "jdbc:h2:mem:test"
  }
  lazy val dbDriverClass = environment match {
    case Production => "org.h2.Driver"
    case Development => "org.h2.Driver"
    case Test => "org.h2.Driver"
  }
  lazy val dbDriver = environment match {
    case Production => scala.slick.driver.H2Driver.simple
    case Development => scala.slick.driver.H2Driver.simple
    case Test => scala.slick.driver.H2Driver.simple
  }

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
