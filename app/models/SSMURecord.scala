package models

import play.api.db.slick.Config.driver.simple._
import play.api.Play.current
import java.sql.Date
import java.text.SimpleDateFormat

case class SSMURecord(id: Option[Int],
        userId: Int,
        name: String,
        weightClass: String,
        sex: String,
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
    def sex = column[String]("SEX", O.NotNull)
    def squat = column[Double]("SQUAT")
    def bench = column[Double]("BENCH")
    def deadlift = column[Double]("DEADLIFT")
    def total = column[Double]("TOTAL")
    def wilks = column[Double]("WILKS")
    def date = column[Date]("DATE")
    def competition = column[Boolean]("COMPETITION")

    def * = (id.?, userId, name, weightClass, sex, squat, bench, deadlift, total, wilks, date, competition) <> ((SSMURecord.apply _).tupled, SSMURecord.unapply)
  
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
    
    def create(userId: Int,
            name: String,
            weightClass: String,
            sex: String,
            squat: Double,
            bench: Double,
            deadlift: Double,
            total: Double,
            wilks: Double,
            date: String,
            competition: Boolean) : Int = {
        import models.HtmlDateToSqlDate._
		play.api.db.slick.DB.withSession{ implicit session =>
			val recordId = (records returning records.map(_.id)) += SSMURecord(None, userId, name, weightClass, sex, squat, bench, deadlift, total, wilks, date, competition)
			recordId
		}
    }
    
    def belongs(id: Int, userId: Int) : Boolean = {
		play.api.db.slick.DB.withSession{ implicit session =>
        	val record = (for{r <- records if r.id === id} yield r).list
        	if(!record.isEmpty) {
        	    record.head.userId == userId
        	} else {
        	    false
        	}
		}
    }
    
    def delete(id: Int)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r
    	q.delete
    	q.invoker.execute
    }
    
    def updateRecord(id: Int,
            //name: String,
            weightClass: String,
            sex: String,
            squat: Double,
            bench: Double,
            deadlift: Double,
            total: Double,
            wilks: Double,
            date: String,
            competition: Boolean)(implicit session: Session) {
        //updateName(id, name)
        updateWeightClass(id, weightClass)
        updateSex(id, sex)
        updateSquat(id, squat)
        updateBench(id, bench)
        updateDeadlift(id, deadlift)
        updateTotal(id, total)
        updateWilks(id, wilks)
        updateDate(id, date)
        updateCompetition(id, competition)

    }
    
    def updateName(id: Int, name: String)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r.name
    	q.update(name)
    	q.invoker.execute
    }
    
    def updateWeightClass(id: Int, weightClass: String)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r.weightClass
    	q.update(weightClass)
    	q.invoker.execute
    }
	
    def updateSex(id: Int, sex: String)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r.sex
    	q.update(sex)
    	q.invoker.execute
    }

    def updateSquat(id: Int, squat: Double)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r.squat
    	q.update(squat)
    	q.invoker.execute
    }

    def updateBench(id: Int, bench: Double)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r.bench
    	q.update(bench)
    	q.invoker.execute
    }

    def updateDeadlift(id: Int, deadlift: Double)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r.deadlift
    	q.update(deadlift)
    	q.invoker.execute
    }

    def updateTotal(id: Int, total: Double)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r.total
    	q.update(total)
    	q.invoker.execute
    }

    def updateWilks(id: Int, wilks: Double)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r.wilks
    	q.update(wilks)
    	q.invoker.execute
    }

    def updateDate(id: Int, date: String)(implicit session: Session) {
        import models.HtmlDateToSqlDate._
    	val q = for { r <- records if r.id === id } yield r.date
    	q.update(date)
    	q.invoker.execute
    }

    def updateCompetition(id: Int, competition: Boolean)(implicit session: Session) {
    	val q = for { r <- records if r.id === id } yield r.competition
    	q.update(competition)
    	q.invoker.execute
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