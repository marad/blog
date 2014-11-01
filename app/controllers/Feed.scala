package controllers

import models.{Db, Post}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.mvc.{Action, Controller}
import sorm.Persisted

object Feed extends Controller {

  import views.Extensions._

  val host = "radoszewski.pl"
  val feedTagDateFormat = DateTimeFormat.forPattern("dd-MM-yyyy:HH-mm")
  val entryTagDateFormat = DateTimeFormat.forPattern("dd-MM-yyyy")

  def feedId = s"tag:$host.${feedTagDateFormat.print(new DateTime)}"
  def tagId(post: Post with Persisted) = s"tag:$host.${entryTagDateFormat.print(new DateTime)}:${post.id}"

//  def generateRss(posts: List[Post with Persisted]) =
//    <rss version="2.0">
//
//      <channel>
//        <title>Moriturius's Devlog</title>
//        <description>My battles with code...</description>
//        <link>http://www.example.com/rss</link>
//        <lastBuildDate>{new DateTime().format()}</lastBuildDate>
//        <language>pl</language>
//
//
//        {
//        for (post <- posts) yield {
//          <item>
//            <title>{post.title}</title>
//            <link>{routes.Posts.view(post.id)}</link>
//            <guid isPermaLink="false">123</guid>
//            <pubDate>{post.date.format()}</pubDate>
//            <description>
//              <![CDATA[ {post.short} ]]>
//            </description>
//
//            {
//            for (tag <- post.tags) yield {
//              <category>
//                <![CDATA[ {tag.name} ]]>
//              </category>
//            }
//            }
//
//          </item>
//        }
//        }
//
//      </channel>
//    </rss>;

  def generateAtom(posts: List[Post with Persisted]) =
    <feed xmlns="http://www.w3.org/2005/Atom">

      <title>moriturius's blog</title>
      <subtitle>My battles with code...</subtitle>
      <link href={"http://" + host + routes.Feed.getAtom.toString} rel="self" />
      <link href={"http://" + host} />
      <id>{feedId}</id>
      <updated>{new DateTime()}</updated>
      <icon>{ routes.Assets.at("images/favicon.png") }</icon>
      <logo>{ routes.Assets.at("images/logo.png") }</logo>

      {
      for (post <- posts) yield {
        <entry>
          <title>{post.title}</title>
          <link href={ s"http://$host${routes.Posts.view(post.id)}" }/>
          <link rel="alternate" type="text/html" href={ s"http://$host${routes.Posts.view(post.id)}" }/>
          <id>{ tagId(post) }</id>
          <updated>{ post.updated }</updated>
          <summary type="xhtml" xml:lang="en"
                   xml:base={s"http://$host/"}>
            { post.short.markupToHtml }
          </summary>
          <content type="xhtml" xml:lang="en"
                   xml:base={s"http://$host/"}>
            { post.content.markupToHtml }
          </content>
          <author>
            <name>Marcin Radoszewski</name>
            <uri>{ s"http://$host/" }</uri>
            <email>moriturius at gmail.com</email>
          </author>
        </entry>
      }
      }
    </feed>;

  def getPosts = Db.query[Post].limit(10).order("date", true).fetch().toList

//  def getRss = Action {
//    Ok(generateRss(getPosts)).as("application/rss+xml")
//  }

  def getAtom = Action {
    Ok(generateAtom(getPosts)).as("application/atom+xml")
  }
}
