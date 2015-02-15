package models

import play.api.db.slick.Config.driver.simple._
import play.api.Play.current
import play.api.db._
import java.sql.Date

case class NewsItem(id: Option[Int], title: String, content: String, author: String, date: Date, userId: Int)

class NewsItems(tag: Tag) extends Table[NewsItem](tag, "NEWS_ITEMS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE", O.NotNull)
    def content = column[String]("CONTENT", O.DBType("TEXT"))
    def author = column[String]("AUTHOR")
    def userId = column[Int]("USER_ID")
    def date = column[Date]("DATE")

	def * = (id.?, title, content, author, date, userId) <> ((NewsItem.apply _).tupled, NewsItem.unapply)

	def user = foreignKey("USER_NEWS_FK", userId, Users.users)(_.id)
}

object NewsItems {
	val news: TableQuery[NewsItems] = TableQuery[NewsItems]

	def populateInit() {
	    import models.HtmlDateToSqlDate._
		play.api.db.slick.DB.withSession{ implicit session =>
			val initNews = List(
			        NewsItem(None, "Dan Green Seminar in Hamilton Ontario", "3 of our members were at the Dan Green seminar in Hamilton Ontario to learn from the boss himself.", "Jonathan Fok kan", "2014-11-29", 1),
			        NewsItem(None, "Saguenay Open 2014", "Jason, Zach, Nicole, Jason and Jonathan were participating in the Quebec Provincials held in Saguenay at Mofo Elite. It was the first competition of several of them. Most did quite well and 2 even brought home medals. Jason came in second in the overalls Junior and Nicole came in first in the Open.", "Jonathan Fok kan", "2014-11-08", 1),
                    NewsItem(None, "Challenge pour le Montreal Children's Hospital", "Xin Di Dong was participating at this meet last weekend and posted a 450.0 kg total. Owing to a shoulder injury he was not able to perform his best but put up a good showing.", "Jonathan Fok kan", "2014-12-06", 1)
				)
			for(newsItem <- initNews) {
				val newsId = (news returning news.map(_.id)) += newsItem
			}
		}
	}

    def submitNews(user: User, title: String, content: String)(implicit session: Session): Int =  {
        val author = user.firstname + " " + user.lastname
        val date: Date = new Date((new java.util.Date()).getTime)
        val newsId = (news returning news.map(_.id)) += NewsItem(None, title, content, author, date, 1)
        newsId
    }
	
	def getNews()(implicit session: Session) : List[NewsItem] = {
        news.sortBy(n => n.date.desc).list
		//(for{n <- news} yield n).list
	}

    def deleteNews(id : Int) (implicit session: Session) {
        val q = for { item <- news if item.id === id } yield item
        q.delete
        q.invoker.execute
    }
}