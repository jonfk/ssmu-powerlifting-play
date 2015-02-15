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
import actions.AuthenticatedActions._

object Application extends Controller {

	def index() = AuthenticatedWithDBAction(optionalLogin = true) { implicit request =>
	    implicit val session = request.dbSession
	    
	    val news = NewsItems.getNews()

        request.user match {
            case Some(user) =>
                Ok(views.html.index(news, user=Some(user))).withSession(request.session)
            case None =>
                Ok(views.html.index(news))
        }
	}

	def about = AuthenticatedWithDBAction(optionalLogin = true) { implicit request =>
	    implicit val session = request.dbSession

	    val team = SSMUProfiles.profiles.list
	    val teamUsers = team.map{ profile =>
	        (for{u <- Users.users if u.id === profile.userId } yield u).list.head
	    }

        request.user match {
            case Some(user) =>
                Ok(views.html.about(team, teamUsers, user=Some(user))).withSession(request.session)
            case None =>
                Ok(views.html.about(team, teamUsers))
        }
	    
	}

	def records = AuthenticatedWithDBAction(optionalLogin = true) { implicit request =>
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

        request.user match {
            case Some(user) =>
                Ok(views.html.records(menMeet, womenMeet, menTraining, womenTraining,mmrUsers,wmrUsers,mtrUsers,wtrUsers, user=Some(user))).withSession(request.session)
            case None =>
                Ok(views.html.records(menMeet, womenMeet, menTraining, womenTraining,mmrUsers,wmrUsers,mtrUsers,wtrUsers))
        }
	}
	
	def notFound = Action { request =>
		Ok(views.html.notFound(request.path))
	}

}