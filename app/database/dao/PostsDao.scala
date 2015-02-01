package database.dao

import config.Config
import config.Config.dbDriver.simple._
import database.Db
import database.model.{DbPostTag, DbPost}
import database.JodaSupport.simple._
import models.{Post, Tag}
import org.joda.time.DateTime

trait PostsDao {
  val db: Db

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
          db.posts.filter(_.id === post.id).update(post.toDbPost)
          id
        case None =>
          db.posts returning db.posts.map(_.id) += post.toDbPost
      }

      removeTagsFromPost(returnedPostId)
      writeNewTags(post.tags)
      connectTagsWithPost(returnedPostId, post.tags)

      returnedPostId
    }

  def deletePost(id:Long): Boolean = db.instance.withTransaction { implicit session =>
    removeTagsFromPost(id)
    1 == db.posts.filter(_.id === id).delete
  }

  def postCount():Int = db.instance.withSession { implicit session =>
    db.posts.length.run
  }

  def listPostsOnFirstPage(): Seq[Post] = listPosts(0, Config.postsPerPage)
  def listPosts(offset:Int, size: Int): Seq[Post] = db.instance.withSession { implicit session =>
    val dbPosts: Seq[DbPost] = db.posts.sortBy(_.created.desc).drop(offset).take(size).list
    dbPosts.map { dbPost =>
      val tags = readAllTagsForPost(dbPost.id.get)
      Post.fromDbPostAndTags(dbPost, tags)
    }
  }

  def listsPostsForPeriod(from:DateTime, to:DateTime): Seq[Post] = {
    db.instance.withTransaction { implicit session =>
      val dbPosts: Seq[DbPost] = db.posts
        .filter(_.created >= from)
        .filter(_.created <= to)
        .sortBy(_.created.desc)
        .list
      dbPosts.map { dbPost =>
        val tags = readAllTagsForPost(dbPost.id.get)
        Post.fromDbPostAndTags(dbPost, tags)
      }
    }
  }

  def listTagsForPost(id: Long): Seq[Tag] = db.instance.withTransaction { implicit session =>
    readAllTagsForPost(id)
  }

  def searchWithPhrase(offset:Int, size:Int, phrases: Seq[String]): Seq[Post] = db.instance.withTransaction { implicit session =>
    val query = createSearchQueryForPhrases(phrases)
    val dbPosts: Seq[DbPost] = query
      .sortBy(_.created.desc)
      .drop(offset)
      .take(size)
      .list

    dbPosts.map { dbPost =>
      val tags = readAllTagsForPost(dbPost.id.get)
      Post.fromDbPostAndTags(dbPost, tags)
    }
  }

  def countAllSearchResults(phrases: Seq[String]): Int = db.instance.withTransaction { implicit session =>
    val query = createSearchQueryForPhrases(phrases)
    query.length.run
  }


  /* ** SEARCH HELPER METHOD ** */
  private def createSearchQueryForPhrases(phrases: Seq[String]) =
    phrases
      .map(createSearchQuery)
      .reduce( (a, b) => a.union(b) )

  private def createSearchQuery(phrase: String) = {
    val preparedPhrase = s"%${phrase.toUpperCase}%"
    db.posts
      .filter( post =>
      (post.title.toUpperCase like preparedPhrase) ||
        (post.content.toUpperCase like preparedPhrase) ||
        (post.extract.toUpperCase like preparedPhrase))
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
