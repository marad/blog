package database

import database.dao.{AccountsDao, PostsDao}

class Dao(val db: Db)
  extends PostsDao
  with AccountsDao
