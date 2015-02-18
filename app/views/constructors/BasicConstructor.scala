package views.constructors

import views.html.helper.FieldConstructor
import views.html.constructors.BasicField

object BasicConstructor {
  implicit val field = FieldConstructor(BasicField.f)
}
