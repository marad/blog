package database.slick

import config.Config
import config.Config.dbDriver._
import org.joda.time.DateTime


case class Tag(id: Option[Long], name: String)
case class Post(id: Option[Long], title: String, extract: String, content: String, created: DateTime, updated: DateTime, tags: Seq[Tag])

class Dao(db: Db) {

  /* ** POSTS ** */
  def readPost(id:Long): Option[Post] = {
    db.instance.withTransaction { implicit session =>
      val result = db.posts.filter(_.id === id).list
      if (result.size > 0) {
        val postData = result(0)

        val tagsForPost = for {
          tp <- db.postTags.filter(_.postId === postData._1)
          t <- db.tags if tp.tagId === t.id
        } yield t.*

        val tags = tagsForPost.list.map(Tag.tupled)
        Some(Post(postData._1, postData._2, postData._3, postData._4, postData._5, postData._6, tags))
      }
      else None
    }

  }
  def writePost(p:Post) = {
    p.id match {
      case Some(id) =>
        db.instance.withTransaction { implicit session =>

          val postData = (p.id, p.title, p.extract, p.content, p.created, p.updated)
          val postTagData = p.tags.map(t => (p.id, t.id))
          val tagData = p.tags.map(t => (t.id, t.name))
          db.posts.update(postData)

          removeTagsFromPost(id)
          p.tags.map(writeTag)
          p.tags.map( t => saveTagToPost(id, t.id))
        }
      case None => throw new RuntimeException("Error while writing data")
    }
  }

  def deletePost(id:Long) = ???

  def listPosts(): List[Post] = listPosts(0, Config.postsPerPage)
  def listPosts(offset:Int, size: Int): List[Post] = ???


  /* ** TAGS ** */
  def readTag(id:Long): Option[Tag] = db.instance.withTransaction { implicit  session =>
    val result = db.tags.filter(_.id === id).list
    if (result.size > 0) Some(Tag.tupled(result(0)))
    else None
  }

  def writeTag(t:Tag) = db.instance.withTransaction { implicit session =>
    t.id match {
      case Some(_) => db.tags.update((t.id, t.name))
      case None => db.tags.insert((t.id, t.name))
    }
  }

  /* ** POST TAGS ** */
  def removeTagsFromPost(postId:Long): Int = db.instance.withTransaction { implicit session =>
    db.postTags.filter(_.postId === postId).delete
  }
  def saveTagToPost(postId:Long, tagId:Long): Int = db.instance.withTransaction { implicit session =>
    db.postTags.insert (Some(postId), Some(tagId))
  }

}
