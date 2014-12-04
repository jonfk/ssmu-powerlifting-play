package controllers

import play.api._
import play.api.mvc._
import models.SSMURecord
import play.api.db._
import models.SSMURecords
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import models.SSMUProfiles
import models.NewsItems
import models.Users

object Application extends Controller {

	def index() = DBAction { implicit request =>
	    implicit val session = request.dbSession
	    
	    val news = NewsItems.getNews()

	    val playSession = request.session
		playSession.get("connected").map { user =>
		    val username = playSession.get("username").get
			Ok(views.html.index(news, loggedIn=true, username=username)).withSession(playSession)
		}.getOrElse {
			//Unauthorized("Oops, you are not connected")
			Ok(views.html.index(news))
		}
	}

	def about = DBAction { implicit request =>
	    implicit val session = request.dbSession
	    val team = SSMUProfiles.profiles.list
	    val teamUsers = team.map{ profile =>
	        (for{u <- Users.users if u.id === profile.userId } yield u).list.head
	    }
	    
	    val playSession = request.session
		playSession.get("connected").map { user =>
		    val username = playSession.get("username").get
			Ok(views.html.about(team, teamUsers, loggedIn=true, username=username)).withSession(playSession)
		}.getOrElse {
			//Unauthorized("Oops, you are not connected")
			Ok(views.html.about(team, teamUsers))
		}
	}

	def records = DBAction { implicit request =>
	    implicit val session = request.dbSession
	    val menMeet = (for{record <- SSMURecords.records if record.sex === "male" && record.competition} yield record).list
	    val womenMeet = (for{record <- SSMURecords.records if record.sex === "female" && record.competition} yield record).list
	    val menTraining = (for{record <- SSMURecords.records if record.sex === "male" && !record.competition} yield record).list
	    val womenTraining = (for{record <- SSMURecords.records if record.sex === "female" && !record.competition} yield record).list
	    val mmrUsers = menMeet.map{ record =>
	        (for{u <- Users.users if u.id === record.userId } yield u).list.head
	    }
	    val wmrUsers = womenMeet.map{ record =>
	        (for{u <- Users.users if u.id === record.userId } yield u).list.head
	    }
	    val mtrUsers = menTraining.map{ record =>
	        (for{u <- Users.users if u.id === record.userId } yield u).list.head
	    }
	    val wtrUsers = womenTraining.map{ record =>
	        (for{u <- Users.users if u.id === record.userId } yield u).list.head
	    }

	    val playSession = request.session
		playSession.get("connected").map { user =>
		    val username = playSession.get("username").get
			Ok(views.html.records(menMeet, womenMeet, menTraining, womenTraining,mmrUsers,wmrUsers,mtrUsers,wtrUsers,loggedIn=true, username=username)).withSession(playSession)
		}.getOrElse {
			Ok(views.html.records(menMeet, womenMeet, menTraining, womenTraining,mmrUsers,wmrUsers,mtrUsers,wtrUsers))
		}
	}
	
	def notFound = Action { request =>
		Ok(views.html.notFound(request.path))
	}

}