package config

import controllers._
import controllers.blog.Pager
import database.{Db, Dao}
import org.slf4j.LoggerFactory

object Config extends play.api.GlobalSettings {

  sealed abstract class Environment
  case object Production extends Environment
  case object Development extends Environment
  case object Test extends Environment

  sealed abstract class DbConfig {
    val driver: String
    val url: String
    val user: String
    val password: String
  }

  case object ProdDb extends DbConfig {
    val driver:String = "org.postgresql.Driver"
    val url:String = System.getProperty("database.url")
    val user:String = System.getProperty("database.user")
    val password:String = System.getProperty("database.pass")
  }

  case object DevDb extends DbConfig {
    val driver = "org.postgresql.Driver"
    val url = "jdbc:postgresql://localhost:5432/blog"
    val user = "blog"
    val password = "blog"
  }

  case object TestDb extends DbConfig {
    val driver = "org.h2.Driver"
    val url = "jdbc:h2:mem:test"
    val user = ""
    val password = ""
  }

  lazy val environment: Environment = {
    System.getProperty("env", "DEV") match {
      case "PROD" =>
        Production
      case "TEST" =>
        Test
      case _ =>
        Development
    }
  }

  lazy val db  = environment match {
    case Production => ProdDb
    case Development => DevDb
    case Test => TestDb
  }

  lazy val dbDriver = environment match {
    case Production => scala.slick.driver.PostgresDriver
    case Development => scala.slick.driver.PostgresDriver
    case Test => scala.slick.driver.H2Driver
  }

  val postsPerPage : Int =
    configuration.getInt("pages.list.postsPerPage") match {
      case Some(value) => value
      case None => 5
    }

  private def getString(settingName: String, defaultValue: String): String =
    configuration.getString(settingName) match {
      case Some(value) => value
      case None => defaultValue
    }

  val database = new Db
  val dao = new Dao(database)

  private val postsController = new Posts(dao, new Pager(postsPerPage))
  private val applicationController = new Application(dao)
  private val feedController = new Feed(dao)
  private val authController = new Auth(dao)

  override def getControllerInstance[A](controllerClass: Class[A]): A = {
    if (controllerClass == classOf[Posts]) postsController.asInstanceOf[A]
    else if (controllerClass == classOf[Application]) applicationController.asInstanceOf[A]
    else if (controllerClass == classOf[Feed]) feedController.asInstanceOf[A]
    else if (controllerClass == classOf[Auth]) authController.asInstanceOf[A]
    else super.getControllerInstance(controllerClass)
  }

}
