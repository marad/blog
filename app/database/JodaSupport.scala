package database

import com.github.tototoshi.slick.GenericJodaSupport
import config.Config
import config.Config.{Development, Production, Test}

object JodaSupport {
  object H2Driver extends GenericJodaSupport(Config.dbDriver)
  object PostgresDriver extends GenericJodaSupport(Config.dbDriver)

  lazy val simple = Config.environment match {
    case Production => PostgresDriver
    case Development => PostgresDriver
    case Test => H2Driver
  }
}
