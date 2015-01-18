package database.slick

import com.github.tototoshi.slick.GenericJodaSupport

object JodaSupport  extends GenericJodaSupport(scala.slick.driver.H2Driver)
