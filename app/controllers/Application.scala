package controllers

import play.api._
import play.api.mvc._
import models.SSMURecord
import play.api.db._
import models.SSMURecords
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction

object Application extends Controller {

	def index = Action {
		Ok(views.html.index())
	}

	def about = Action {
		Ok(views.html.about())
	}

	def records = DBAction { implicit request =>
	    implicit val session = request.dbSession
	    val data = SSMURecords.records.list
	    val menMeet = (for{record <- SSMURecords.records if record.gender === "male" && record.meet} yield record)
	    val womenMeet = (for{record <- SSMURecords.records if record.gender === "female" && record.meet} yield record)
	    val menTraining = (for{record <- SSMURecords.records if record.gender === "male" && !record.meet} yield record)
	    val womenTraining = (for{record <- SSMURecords.records if record.gender === "female" && !record.meet} yield record)
		Ok(views.html.records(menMeet.list, womenMeet.list, menTraining.list, womenTraining.list))
	}

	def notFound = Action {
		Ok(views.html.notFound())
	}

	/* Old stuff */
	def oldindex = Action {
		Ok(views.html.oldindex("Your new application is ready."))
	}

	def scaladoc = Action {
		Ok(views.html.doc("Welcome to Play"))
	}
}