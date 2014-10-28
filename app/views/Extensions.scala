package views

import org.joda.time.{ReadableInstant, DateTime}
import org.joda.time.format.DateTimeFormat

object Extensions {
  class JodaTimeWrapper[T <: ReadableInstant](t:T) {
    def format(pattern: String): String = {
      val fmt = DateTimeFormat.forPattern(pattern)
      fmt.print(t)
    }
  }
  implicit def convertToJodaTimeWrapper[T <: ReadableInstant](t:T) = new JodaTimeWrapper(t)
}
