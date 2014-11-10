package config

import play.api.Play

object Config {
  def postsPerPage : Int = 
    Play.current.configuration.getString("pages.list.postsPerPage") match {
      case Some(value) => value.toInt
      case None => 10
    }
}
