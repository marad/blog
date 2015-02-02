package models

import database.model.DbAccount
import play.api.libs.json.Json

case class Account(id: Option[Long], username: String, password: String) {
  def toDbAccount = DbAccount(id, username, password)
}

object Account {
  implicit val accountFormat = Json.format[Account]
  def fromDbAccount(dbAcc: DbAccount) = Account(dbAcc.id, dbAcc.username, dbAcc.password)
}
