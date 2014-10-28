package models

import play.api.libs.json.Json

case class Account(name: String, password: String, email: String)

object Account {
  implicit val accountFormat = Json.format[Account]
}
