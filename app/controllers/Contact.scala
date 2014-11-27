package controllers

import play.api._
import play.api.mvc._
import models.SSMURecord
import play.api.db._
import models.SSMURecords
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import play.twirl.api.Html

object Contact extends Controller {

	def contact = Action { request =>

	    val playSession = request.session
		playSession.get("connected").map { user =>
		    val username = playSession.get("username").get
			Ok(views.html.contact(loggedIn=true, username=username)).withSession(playSession)
		}.getOrElse {
			//Unauthorized("Oops, you are not connected")
			Ok(views.html.contact())
		}

	}

	def contactPost = Action { request =>
	    println(request.body)
	    Ok("Success")
	}
}