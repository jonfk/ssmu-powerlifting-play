package controllers

import play.api._
import play.api.mvc._
import models.SSMURecord
import play.api.db._
import models.SSMURecords
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import models.SSMUProfiles
import play.api.data._
import play.api.data.Forms._
import org.apache.commons.validator.routines.EmailValidator
import models.Users
import models.User
import play.api.Play.current

object Signin extends Controller {
	def signin = Action { implicit request =>

	    val playSession = request.session
		playSession.get("connected").map { email =>
		    Ok("you are already signed in")
		}.getOrElse {
			Ok(views.html.signin())
		}
	}
	
	val loginForm = Form(
			tuple(
					"emailOrUsername" -> text,
					"password" -> text,
					"remember" -> optional(number)
					)
			)
	
	def loginPost = Action { implicit request =>
	    println(request.body)
	    val (emailOrUsername, password, remember) = loginForm.bindFromRequest.get
	    val validator = EmailValidator.getInstance()
	    println("password: " + password)
		play.api.db.slick.DB.withSession{ implicit session =>
	    	println(Users.users.list)
	    }
	    if(Users.authenticateEmail(emailOrUsername, password) || Users.authenticateUsername(emailOrUsername, password)) {
	        val user = Users.getUser(emailOrUsername).get
	        Redirect(routes.Application.index()).withSession("connected" -> user.email, "username" -> user.username)
	    } else {
	    	Ok("Authentication Error")
	    }
	}
	
	def logout = Action {
	        Redirect(routes.Application.index()).withNewSession
	}

	val signupForm = Form(
			tuple(
					"email" -> text,
					"username" -> text,
					"firstname" -> text,
					"lastname" -> text,
					"passwd" -> text,
					"icode" -> text
					)
			)
	
	def signup = DBAction { implicit request =>
	    println("Received signup request")
	    println(request.body)
	    
	    implicit val session = request.dbSession

	    val (email, username, firstname, lastname, password, invitecode) = signupForm.bindFromRequest.get
	    
	    if(invitecode == "iwantadmin") {
	        Users.addUser(email, username, firstname, lastname, password, power="admin")
	    	Ok(views.html.signin(success=Some("You are now added as an admin user")))
	    } else if(invitecode == "agentvalentine") {
	        Users.addUser(email, username, firstname, lastname, password)
	    	Ok(views.html.signin(success=Some("You are now added as a user")))
	    } else {
	    	Ok(views.html.signin(error=Some("sorry unauthorized to create an account. Contact administrator to verify you should have access to this feature")))
	    }

	}
}