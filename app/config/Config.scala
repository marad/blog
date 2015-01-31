package config

import controllers.{Feed, Posts, Application}
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
    // TODO: get settings from ENVIRONMENT
    val driver = "org.postgresql.Driver"
    val url = getString("db.url", "db.url is not set")
    val user = getString("db.user", "db.user is not set")
    val password = getString("db.password", "db.password is not set")
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

  lazy val environment: Environment =
    System.getProperty("env") match {
      case "PROD" => Production
      case "TEST" => Test
      case _ => Development
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

  val logger = LoggerFactory.getLogger(Config.getClass)
  logger.info(s"Trying to connect to database: ${db.url} as ${db.user}")

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

  private def getString(settingName: String, defaultValue: String) =
    configuration.getString(settingName) match {
      case Some(value) => value
      case None => defaultValue
    }
}
