package database

trait DatabaseTest {
  def withDao(testCode: Dao => Any) {
    val db = new Db with DbTestData
    val daoUnderTest = new Dao(db)

    try {
      testCode(daoUnderTest)
    }
    finally db.connection.close()
  }

  def withDatabase(testCode: => Any) {
    val db = new Db with DbTestData
    try { testCode }
    finally db.connection.close()
  }
}
