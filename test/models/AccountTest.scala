package models

import org.scalatest.{Matchers, FlatSpec}

class AccountTest extends FlatSpec with Matchers {

  "Account" should "properly hash and check passwords" in {

    val plain = new Account(None, "user", "password")
    val hashed = plain.hashPassword

    val correctPasswordUsed = hashed.checkPassword("password")
    val incorrectPasswordUsed = hashed.checkPassword("incorrect")

    hashed.password should not equal plain.password
    correctPasswordUsed shouldBe true
    incorrectPasswordUsed shouldBe false
  }

}
