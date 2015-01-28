package database.slick

import config.Config
import config.Config.dbDriver._
import org.joda.time.LocalDate
import slickmodels.{Post, Tag}

class Dao(db: Db) {

  /* ** POSTS ** */
  def readPost(id:Long): Option[Post] =
    db.instance.withTransaction { implicit session =>
      val result = db.posts.filter(_.id === id).list
      result match {
        case dbPost :: _ =>
          val tags = readAllTagsForPost(dbPost.id.get)
          Some(Post.fromDbPostAndTags(dbPost, tags))
        case _ =>
          None
      }
    }

  def writePost(post:Post): Long =
      db.instance.withTransaction { implicit session =>
        val returnedPostId:Long = post.id match {
          case Some(id) =>
            db.posts.update(post.toDbPost)
            id
          case None =>
            db.posts returning db.posts.map(_.id) += post.toDbPost
        }

        removeTagsFromPost(returnedPostId)
        writeNewTags(post.tags)
        connectTagsWithPost(returnedPostId, post.tags)

        returnedPostId
      }

  def deletePost(id:Long): Int = db.instance.withTransaction { implicit session =>
    removeTagsFromPost(id)
    db.posts.filter(_.id === id).delete
  }

  def postCount(): Int = ???

  def listPosts(): Seq[Post] = listPosts(0, Config.postsPerPage)
  def listPosts(offset:Int, size: Int): Seq[Post] = db.instance.withSession { implicit session =>
    val dbPosts: Seq[DbPost] = db.posts.sortBy(_.created.desc).drop(offset).take(size).list
    dbPosts.map { dbPost =>
      val tags = readAllTagsForPost(dbPost.id.get)
      Post.fromDbPostAndTags(dbPost, tags)
    }
  }

  def listsPostsForPeriod(from:LocalDate, to:LocalDate): Seq[Post] = ???

  def listTagsForPost(id: Long): Seq[Tag] = db.instance.withTransaction { implicit session =>
    readAllTagsForPost(id)
  }

  /* ** TAGS ** */
  private def filterExistingTags(tags: Seq[Tag])(implicit session: Session): Seq[Tag] = {
    val names = tags.map(_.name)
    val existingTags = db.tags.filter(_.name inSet names).map(_.name).list.toSet
    tags.filterNot { tag: Tag =>
      existingTags.contains(tag.name)
    }
  }

  private def writeNewTags(tags: Seq[Tag])(implicit session: Session): Unit =
    filterExistingTags(tags).map (db.tags += _.toDbTag)

  private def readAllTagsForPost(postId: Long)(implicit session: Session): Seq[Tag] =
    (for {
      tp <- db.postTags.filter(_.postId === postId)
      t <- db.tags.filter(tp.tagId === _.id)
    } yield t).list.map(Tag.fromDbTag)


  /* ** POST TAGS ** */
  private def removeTagsFromPost(postId:Long)(implicit session:Session): Int =
    db.postTags.filter(_.postId === postId).delete

  private def connectTagsWithPost(postId: Long, tags: Seq[Tag])(implicit session: Session): Unit = {
    val tagNames = tags.map(_.name).toSet
    val dbTags = db.tags.filter(_.name inSet tagNames).list
    val toSave = dbTags.map {
      tag => DbPostTag(postId, tag.id.get)
    }
    db.postTags ++= toSave
  }
}
