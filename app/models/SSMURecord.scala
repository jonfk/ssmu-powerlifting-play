package models

import play.api.db.slick.Config.driver.simple._
import play.api.Play.current

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
        meet: Boolean)
        
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
    def meet = column[Boolean]("MEET")

    def * = (id.?, userId, name, weightClass, gender, squat, bench, deadlift, total, wilks, meet) <> ((SSMURecord.apply _).tupled, SSMURecord.unapply)
  
    def user = foreignKey("USER_RECORD_FK", userId, Users.users)(_.id)
}

object SSMURecords {
	val records: TableQuery[SSMURecords] = TableQuery[SSMURecords]
	
	def populateInit() {
		play.api.db.slick.DB.withSession{ implicit session =>
			val currentRecords = List(
				SSMURecord(None, 1, "Jonathan Fok kan", "-66kg", "male", 125, 80, 155, 360, 290, true),
				SSMURecord(None, 1, "Jason Delatolla", "-83kg", "male", 215, 125,255,592.5,401, true)
				)
			for(record <- currentRecords) {
				records += record
			}
		}
	}
}