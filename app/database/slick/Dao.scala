package database.slick

import config.Config
import config.Config.dbDriver._
import slickmodels.{Post, Tag}
import utils.TupleOps._

class Dao(db: Db) {

  /* ** POSTS ** */
  def readPost(id:Long): Option[Post] =
    db.instance.withTransaction { implicit session =>
      val result = db.posts.filter(_.id === id).list
      if (result.size > 0) {
        val rawPost = result(0)

        val tagsForPost = for {
          tp <- db.postTags.filter(_.postId === rawPost.id)
          t <- db.tags.filter(tp.tagId === _.id)
        } yield t

        val tags = tagsForPost.list.map(Tag.fromDbTag)

        DbPost.unapply(rawPost) match {
          case Some(data) => Some(Post.tupled(data :+ tags))
          case _ => None
        }
      }
      else None
    }

  def writePost(post:Post): Long =
      db.instance.withTransaction { implicit session =>
        val dbPost = post.toDbPost

        val returnedPostId:Long = post.id match {
          case Some(id) =>
            db.posts.update(dbPost)
            id
          case None =>
            db.posts returning db.posts.map(_.id.get) += dbPost
        }

        removeTagsFromPost(returnedPostId)
        try {
          post.tags.map(writeTag)
        } catch { case _:SlickException => }
        post.tags.map(_.id).map {
          case Some(tagId) => saveTagToPost(returnedPostId, tagId)
          case _ =>
        }
        returnedPostId
      }

  def deletePost(id:Long) = db.instance.withTransaction { implicit session =>

  }

  def listPosts(): List[Post] = listPosts(0, Config.postsPerPage)
  def listPosts(offset:Int, size: Int): List[Post] = ???


  /* ** TAGS ** */
  def readTag(id:Long): Option[DbTag] = db.instance.withTransaction { implicit  session =>
    val result = db.tags.filter(_.id === id).list
    if (result.size > 0) Some(result(0))
    else None
  }

  def writeTag(t:Tag): Long = db.instance.withTransaction { implicit session =>
    t.id match {
      case Some(tagId) =>
        db.tags.update(t.toDbTag)
        tagId
      case None =>
        (db.tags returning db.tags.map(_.id.get)) += t.toDbTag
    }
  }

//  def writeTags(tags: Seq[Tag]):

  /* ** POST TAGS ** */
  def removeTagsFromPost(postId:Long): Int = db.instance.withTransaction { implicit session =>
    db.postTags.filter(_.postId === postId).delete
  }
  def saveTagToPost(postId:Long, tagId:Long): Int = db.instance.withTransaction { implicit session =>
    db.postTags.insert(DbPostTag(postId, tagId))
  }

}
