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

	def index() = Action { implicit request =>
	    val session = request.session
		session.get("connected").map { user =>
		    val username = session.get("username").get
			Ok(views.html.index(loggedIn=true, username=username)).withSession(session)
		}.getOrElse {
			//Unauthorized("Oops, you are not connected")
			Ok(views.html.index())
		}
	}

	def about = DBAction { implicit request =>
	    implicit val session = request.dbSession
	    val team = SSMUProfiles.profiles.list
	    println(team)
	    
	    val playSession = request.session
		playSession.get("connected").map { user =>
		    val username = playSession.get("username").get
			Ok(views.html.about(team, loggedIn=true, username=username)).withSession(playSession)
		}.getOrElse {
			//Unauthorized("Oops, you are not connected")
			Ok(views.html.about(team))
		}
	}

	def records = DBAction { implicit request =>
	    implicit val session = request.dbSession
	    val menMeet = (for{record <- SSMURecords.records if record.sex === "male" && record.competition} yield record)
	    val womenMeet = (for{record <- SSMURecords.records if record.sex === "female" && record.competition} yield record)
	    val menTraining = (for{record <- SSMURecords.records if record.sex === "male" && !record.competition} yield record)
	    val womenTraining = (for{record <- SSMURecords.records if record.sex === "female" && !record.competition} yield record)

	    val playSession = request.session
		playSession.get("connected").map { user =>
		    val username = playSession.get("username").get
			Ok(views.html.records(menMeet.list, womenMeet.list, menTraining.list, womenTraining.list, loggedIn=true, username=username)).withSession(playSession)
		}.getOrElse {
			Ok(views.html.records(menMeet.list, womenMeet.list, menTraining.list, womenTraining.list))
		}
	}
	
	def notFound = Action { request =>
		Ok(views.html.notFound(request.path))
	}

}