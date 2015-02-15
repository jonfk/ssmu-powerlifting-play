package controllers

import play.api._
import play.api.mvc._
import models.SSMURecord
import play.api.db._
import models.SSMURecords
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import play.twirl.api.Html
import actions.AuthenticatedActions._

object Contact extends Controller {

	def contact = Authenticated(optionalLogin = true) { request =>

        request.user match {
            case Some(user) =>
                Ok(views.html.contact(user=Some(user))).withSession(request.session)
            case None =>
                Ok(views.html.contact())
        }
	}

	def contactPost = Action { request =>
	    println(request.body)
	    Ok("Success")
	}
}