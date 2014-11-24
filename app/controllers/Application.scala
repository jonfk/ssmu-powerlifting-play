package controllers

import play.api._
import play.api.mvc._
import models.SSMURecord
import play.api.db._
import models.SSMURecords
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import models.SSMUProfiles

object Application extends Controller {

	def index = Action {
		Ok(views.html.index())
	}

	def about = DBAction { request =>
	    implicit val session = request.dbSession
	    val team = SSMUProfiles.profiles.list
	    println(team)
		Ok(views.html.about(team))
	}

	def records = DBAction { implicit request =>
	    implicit val session = request.dbSession
	    val menMeet = (for{record <- SSMURecords.records if record.gender === "male" && record.meet} yield record)
	    val womenMeet = (for{record <- SSMURecords.records if record.gender === "female" && record.meet} yield record)
	    val menTraining = (for{record <- SSMURecords.records if record.gender === "male" && !record.meet} yield record)
	    val womenTraining = (for{record <- SSMURecords.records if record.gender === "female" && !record.meet} yield record)
		Ok(views.html.records(menMeet.list, womenMeet.list, menTraining.list, womenTraining.list))
	}
	
	def contact = Action {
	    Ok(views.html.contact())
	}

	def notFound = Action { request =>
		Ok(views.html.notFound(request.path))
	}

	/* Old stuff */
	def oldindex = Action {
		Ok(views.html.oldindex("Your new application is ready."))
	}

	def scaladoc = Action {
		Ok(views.html.doc("Welcome to Play"))
	}
}