package controllers.blog

import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.{Matchers, FlatSpec}

class PagerTest extends FlatSpec with Matchers
with TableDrivenPropertyChecks {

  val pager = new Pager(5)

  val cases = Table(
    ("items", "expectedPages"),
    (0, 1),
    (5, 1),
    (6, 2),
    (10, 2),
    (11, 3)
  )

  "Pager" should "properly count pages" in {
    forAll(cases) { (items, expectedPage) =>
      val pageCount = pager.countMaxPages(items)
      pageCount shouldBe expectedPage
    }
  }
}
