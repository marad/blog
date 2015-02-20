import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.scalatest.selenium.WebBrowser
import org.scalatest.{Matchers, FlatSpec}
import play.api.test._
import play.api.test.Helpers._
import database.DatabaseTest
import org.scalatestplus.play._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends FlatSpec with Matchers
with DatabaseTest with OneServerPerSuite with WebBrowser {


  implicit val webDriver = new HtmlUnitDriver

  "Application" should "work from within a browser" in withDatabase {
    go to s"http://localhost:$port"
    pageSource contains "O mnie"
    pageSource contains "Muzyka"
    pageSource contains "Programy"
    pageSource contains "Gry"
  }
}
