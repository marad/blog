package controllers

import org.scalatest.FlatSpec
import org.scalatestplus.play.OneAppPerSuite
import play.api.http.{HeaderNames, Status}
import play.api.mvc.Results
import play.api.test._

trait ControllerSpec extends FlatSpec with Results with ResultExtractors with Status with HeaderNames with DefaultAwaitTimeout with OneAppPerSuite {
}
