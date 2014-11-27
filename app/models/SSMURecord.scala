package models

import play.api.db.slick.Config.driver.simple._
import play.api.Play.current
import java.sql.Date
import java.text.SimpleDateFormat

case class SSMURecord(id: Option[Int],
        userId: Int,
        name: String,
        weightClass: String,
        gender: String,
        squat: Double,
        bench: Double,
        deadlift: Double,
        total: Double,
        wilks: Double,
        date: Date,
        competition: Boolean)
        
class SSMURecords(tag: Tag) extends Table[SSMURecord](tag, "SSMU_RECORDS") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def userId = column[Int]("USER_ID")
    def name = column[String]("NAME")
    def weightClass = column[String]("WEIGHT_CLASS", O.NotNull)
    def gender = column[String]("GENDER", O.NotNull)
    def squat = column[Double]("SQUAT")
    def bench = column[Double]("BENCH")
    def deadlift = column[Double]("DEADLIFT")
    def total = column[Double]("TOTAL")
    def wilks = column[Double]("WILKS")
    def date = column[Date]("DATE")
    def competition = column[Boolean]("COMPETITION")

    def * = (id.?, userId, name, weightClass, gender, squat, bench, deadlift, total, wilks, date, competition) <> ((SSMURecord.apply _).tupled, SSMURecord.unapply)
  
    def user = foreignKey("USER_RECORD_FK", userId, Users.users)(_.id)
}

object SSMURecords {
	// For String to Date implicit conversion
    import models.HtmlDateToSqlDate._

	val records: TableQuery[SSMURecords] = TableQuery[SSMURecords]
	
	def populateInit() {
		play.api.db.slick.DB.withSession{ implicit session =>
			val currentRecords = List(
				SSMURecord(None, 1, "Jonathan Fok kan", "-66kg", "male", 125, 80, 155, 360, 290, "2013-01-28", true),
				SSMURecord(None, 1, "Jason Delatolla", "-83kg", "male", 215, 125,255,592.5,401, "2014-10-30", true),
				SSMURecord(None, 1, "Jake Sha", "+100kg", "male", 125, 80, 155, 360, 290,  "1992-01-11", false)
				)
			for(record <- currentRecords) {
				records += record
			}
		}
	}
	
}

object HtmlDateToSqlDate {
    implicit def htmlDateToSqlDate(htmlDate: String) : Date = {
	    val format = new SimpleDateFormat("yyyy-MM-dd")
	    val date = format.parse("2013-01-28")
	    val sqlDate = new Date(date.getTime()) 
	    sqlDate
    }
}