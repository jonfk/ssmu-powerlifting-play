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

	def contact = Action {
	    Ok(views.html.contact())
	}

	def contactPost = Action { request =>
	    println(request.body)
	    Ok("Success")
	}
}