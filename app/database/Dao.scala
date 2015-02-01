package database

import database.dao.PostsDao

class Dao(val db: Db) extends PostsDao
