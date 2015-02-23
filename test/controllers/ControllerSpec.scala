package controllers

import org.scalatest.Matchers
import org.scalatest.mock.MockitoSugar
import play.api.http.{HeaderNames, Status}
import play.api.mvc.Results
import play.api.test._

trait ControllerSpec
  extends Results
  with ResultExtractors
  with Status
  with HeaderNames
  with DefaultAwaitTimeout
  with Matchers
  with MockitoSugar

