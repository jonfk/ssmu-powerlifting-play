package models

import play.api.db.slick.Config.driver.simple._
import play.api.Play.current

case class User(username: String, email: String, password: String)

case class SSMURecord(name: String,
        gender: String,
        weightClass: String,
        squat: Double,
        bench: Double,
        deadlift: Double,
        total: Double,
        wilks: Double,
        meet: Boolean)
        
class SSMURecords(tag: Tag) extends Table[SSMURecord](tag, "SSMU_RECORDS") {
  def name = column[String]("NAME", O.PrimaryKey)
  def weightClass = column[String]("WEIGHT_CLASS", O.NotNull)
  def gender = column[String]("GENDER", O.NotNull)
  def squat = column[Double]("SQUAT")
  def bench = column[Double]("BENCH")
  def deadlift = column[Double]("DEADLIFT")
  def total = column[Double]("TOTAL")
  def wilks = column[Double]("WILKS")
  def meet = column[Boolean]("MEET")

  def * = (name, weightClass, gender, squat, bench, deadlift, total, wilks, meet) <> ((SSMURecord.apply _).tupled, SSMURecord.unapply)
}

object SSMURecords {
	val records: TableQuery[SSMURecords] = TableQuery[SSMURecords]
	
	def populateInit() {
		play.api.db.slick.DB.withSession{ implicit session =>
			val currentRecords = List(
				SSMURecord("Jonathan Fok kan", "-66kg", "male", 125, 80, 155, 360, 290, true),
				SSMURecord("Jason Delatolla", "-83kg", "male", 215, 125,255,592.5,401, true)
				)
			for(record <- currentRecords) {
				records += record
			}
		}
	}
}