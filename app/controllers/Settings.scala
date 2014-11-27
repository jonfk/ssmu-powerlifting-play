package controllers

import play.api._
import play.api.mvc._
import models.SSMURecord
import play.api.db._
import models.SSMURecords
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import play.twirl.api.Html
import models._
import play.api.Play

object Settings extends Controller {

	def settings = DBAction { request =>
	    implicit val session = request.dbSession

	    val playSession = request.session
		playSession.get("connected").map { user =>
		   	val username = playSession.get("username").get
		    val user = Users.getUser(username).getOrElse(User(None,"","","","",""))
		    //println("user id : " + user.id)
		    //println(SSMUProfiles.profiles.list)
		    if(SSMUProfiles.exists(user.id.get)) {
		        val profile = SSMUProfiles.getProfile(user.id.get)

		    	Ok(views.html.userSettings(user, Some(profile))).withSession(playSession)
		    } else {
		    	Ok(views.html.userSettings(user, None)).withSession(playSession)
		    }
	    }.getOrElse{
			Unauthorized("Oops, you are not connected")
	    }
	}
	
	def profileUpdate = DBAction(parse.multipartFormData) { implicit request =>
	    println("profile update")
	    println(request.body)
	    
	    implicit val session = request.dbSession
	    val values = request.body.asFormUrlEncoded.map{ case (name, value) => (name, value.mkString(""))}
	    
	    val playSession = request.session
		playSession.get("connected").map { user =>
		   	val username = playSession.get("username").get
		    val user = Users.getUser(username).getOrElse(User(None,"","","","",""))
		    
		    val imageUrl : String = 
		        request.body.file("image").map{ image =>
		   	    	import java.io.File
		   	    	val filename = username +"-"+ image.filename
		   	    	val path = s"public/images/profiles/$filename"
		   	    	val url = s"assets/images/profiles/$filename"
		   	    	image.ref.moveTo(new File(path), true)
		   	    	url
		   	}.getOrElse(Play.current.configuration.getString("image.default.url").get)

		    if(values.get("hasProfile").get == "false") {
		    	// Create profile
		        println("creating profile")
		        SSMUProfiles.create(user.id.get,
		                values.get("name").get,
		                values.get("description").get,
		                imageUrl,
		                values.get("weightClass").get,
		                values.get("facebook").get,
		                values.get("youtube").get,
		                values.get("instagram").get)
		    } else {
		    	// Update profile
		        println("updating profile")
		        SSMUProfiles.updateProfile(user.id.get,
		               values.get("name").get,
		               values.get("description").get,
		               imageUrl,
		               values.get("weightClass").get,
		               values.get("facebook").get,
		               values.get("youtube").get,
		               values.get("instagram").get)
		    }
		    
	        Redirect(routes.Settings.settings)
	    }.getOrElse{
			Unauthorized("Oops, you are not connected")
	    }
	}
	
	def recordUpdate = Action { request =>
	    println("record update")
	    println(request.body)
	    Ok("recordUpdate received")
	    
	}

	def userUpdate = DBAction(parse.urlFormEncoded) { implicit request =>
	    println("user update")
	    println(request.body)
	    Ok("userUpdate received")
	    
	    
	    val playSession = request.session
		playSession.get("connected").map { user =>
		   	val username = playSession.get("username").get
		    val user = Users.getUser(username).getOrElse(User(None,"","","","",""))
		    
		    // Update values
		    implicit val session = request.dbSession
		    
		    request.body.get("email").get.head match {
		    	case "" =>
		    		println("empty email; do nothing")
		    	case email =>
		    	    Users.updateEmail(user.id, email)
		    		//val t = (for{u <- Users.users if u.id === user.id } yield u.email)
		    		//println(t.invoker.list)
		   	}
		    request.body.get("username").get.head match {
		    	case "" =>
		    		println("empty username; do nothing")
		    	case newUsername =>
		    	    Users.updateUsername(user.id, newUsername)
		    		//val t = (for{u <- Users.users if u.id === user.id } yield u.username)
		    		//println(t.invoker.list)
		   	}
		    request.body.get("firstname").get.head match {
		    	case "" =>
		    		println("empty firstname; do nothing")
		    	case firstname =>
		    	    Users.updateFirstname(user.id, firstname)
		    		//val t = (for{u <- Users.users if u.id === user.id } yield u.firstname)
		    		//println(t.invoker.list)
		   	}
		    request.body.get("lastname").get.head match {
		    	case "" =>
		    		println("empty lastname; do nothing")
		    	case lastname =>
		    	    Users.updateLastname(user.id, lastname)
		    		//val t = (for{u <- Users.users if u.id === user.id } yield u.lastname)
		    		//println(t.invoker.list)
		   	}
		    request.body.get("password").get.head match {
		    	case "" =>
		    		println("empty password; do nothing")
		    	case password =>
		    	    Users.updatePassword(user.id, password)
		    		//val t = (for{u <- Users.users if u.id === user.id } yield u.password)
		    		//println(t.invoker.list)
		   	}

			//Ok(views.html.userSettings(user)).withSession(playSession)
	        Redirect(routes.Settings.settings)
	    }.getOrElse{
	        println("Unauthorized access")
			Unauthorized("Oops, you are not connected")
	    }
	}

}