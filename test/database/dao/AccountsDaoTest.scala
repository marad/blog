package database.dao

import database.{DbTestData, DatabaseTest}
import models.Account
import org.scalatest.{Matchers, FlatSpec}

class AccountsDaoTest extends FlatSpec with DatabaseTest with Matchers {

  "AccountsDao" should "read accounts from database" in withDao { dao =>
    val fa = dao.findAccount(1l)
    val sa = dao.findAccount("Second")
    val notExisting = dao.findAccount(12345l)

    fa shouldBe Some(AccountsDaoTest.firstAccount)
    sa shouldBe Some(AccountsDaoTest.secondAccount)
    notExisting shouldBe None
  }

  it should "write accounts to database" in withDao { dao =>
    val changedAccountId = dao.saveAccount(AccountsDaoTest.firstAccount.copy(username = "Other"))
    val newAccountId = dao.saveAccount(AccountsDaoTest.accToSave)

    val changedAccount = dao.findAccount(changedAccountId).get
    val newAccount = dao.findAccount(newAccountId).get

    newAccountId shouldBe 4l
    newAccount shouldBe AccountsDaoTest.accToSave.copy(id = Some(4l))
    changedAccount.id shouldBe AccountsDaoTest.firstAccount.id
    changedAccount.username shouldBe "Other"
  }

  it should "delete accounts from database" in withDao { dao =>
    val existingDeleted = dao.deleteAccount(1l)
    val notExistingDeleted = dao.deleteAccount(12345l)
    val deletedAccount = dao.findAccount(1l)

    existingDeleted shouldBe true
    notExistingDeleted shouldBe false
    deletedAccount shouldBe None
  }
}

object AccountsDaoTest {
  val firstAccount = Account.fromDbAccount(DbTestData.firstAccount)
  val secondAccount = Account.fromDbAccount(DbTestData.secondAccount)
  val thirdAccount = Account.fromDbAccount(DbTestData.thirdAccount)

  val accToSave = Account(None, "Fourth", "Acc4")
}
