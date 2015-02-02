package models

import database.model.DbAccount
import org.mindrot.jbcrypt.BCrypt
import play.api.libs.json.Json

case class Account(id: Option[Long], username: String, password: String) {
  def toDbAccount = DbAccount(id, username, password)
  def hashPassword: Account = copy(password = BCrypt.hashpw(password, BCrypt.gensalt()))
  def checkPassword(toCheck: String) = BCrypt.checkpw(toCheck, password)
}

object Account {
  implicit val accountFormat = Json.format[Account]
  def fromDbAccount(dbAcc: DbAccount) = Account(dbAcc.id, dbAcc.username, dbAcc.password)
}
