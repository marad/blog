package database.dao

import database.Db
import database.model.DbAccount
import models.Account
import config.Config.dbDriver.simple._

trait AccountsDao {
  val db: Db

  def findAccount(username: String): Option[Account] = db.instance.withTransaction { implicit session =>
    val result = db.accounts.filter(_.username === username).list
    extractFindResult(result)
  }

  def findAccount(id: Long): Option[Account] = db.instance.withTransaction { implicit session =>
    val result = db.accounts.filter(_.id === id).list
    extractFindResult(result)
  }

  def saveAccount(acc: Account): Long = db.instance.withTransaction { implicit session =>
    acc.id match {
      case Some(id) =>
        db.accounts.filter(_.id === id).update(acc.toDbAccount)
        id
      case None =>
        db.accounts returning db.accounts.map(_.id) += acc.toDbAccount
    }
  }

  def deleteAccount(id: Long): Boolean = db.instance.withTransaction { implicit session =>
    1 == db.accounts.filter(_.id === id).delete
  }

  private def extractFindResult(result: List[DbAccount]): Option[Account] = result match {
    case dbAcc :: _ => Some(Account.fromDbAccount(dbAcc))
    case _ => None
  }

}
