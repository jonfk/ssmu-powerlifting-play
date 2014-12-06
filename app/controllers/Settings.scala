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
import play.api.data._
import play.api.data.Forms._

object Settings extends Controller {

	def settings = DBAction { request =>
	    implicit val session = request.dbSession

	    val playSession = request.session
		playSession.get("connected").map { user =>
		   	val username = playSession.get("username").get
		    val user = Users.getUser(username).getOrElse(User(None,"","","","",""))
		    //println("user id : " + user.id)
		    //println(SSMUProfiles.profiles.list)
		    val records = (for{r <- SSMURecords.records if r.userId === user.id} yield r).list
		    if(SSMUProfiles.exists(user.id.get)) {
		        val profile = SSMUProfiles.getProfile(user.id.get)

		    	Ok(views.html.userSettings(user, Some(profile), records)).withSession(playSession)
		    } else {
		    	Ok(views.html.userSettings(user, None, records)).withSession(playSession)
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
		   	    	val extension = image.contentType.map(imagetype => imagetype.split("/").last).getOrElse("jpg")
		   	    	val filename = username +"."+ extension
		   	    	val path = s"images/profiles/$filename"
		   	    	val url = s"/profiles/images/$filename"
		   	    	image.ref.moveTo(new File(path), true)
		   	    	url
		   	}.getOrElse(Play.current.configuration.getString("image.default.url").get)

		    if(values.get("hasProfile").get == "false") {
		    	// Create profile
		        println("creating profile")
		        SSMUProfiles.create(user.id.get,
		                values("name"),
		                values("description"),
		                imageUrl,
		                values("weightClass"),
		                values("facebook"),
		                values("youtube"),
		                values("instagram"))
		    } else {
		    	// Update profile
		        println("updating profile")
		        SSMUProfiles.deleteImageFile(user.id.get)
		        SSMUProfiles.updateProfile(user.id.get,
		               values("name"),
		               values("description"),
		               imageUrl,
		               values("weightClass"),
		               values("facebook"),
		               values("youtube"),
		               values("instagram"))
		    }
		    
	        Redirect(routes.Settings.settings)
	    }.getOrElse{
			Unauthorized("Oops, you are not connected")
	    }
	}
	
	case class RecordData(
	        id: Option[String],
	        sex: String,
	        weightClass: String,
	        unit: String,
	        bodyweight: String,
	        squat: String,
	        bench: String,
	        deadlift: String,
	        wilks: String,
	        date: String,
	        context: String
	)
	val recordForm = Form(
		mapping(
		    "id" -> optional(text),
			"sex" -> text,
			"weightClass" -> text,
			"unit" -> text,
			"bodyweight" -> text,
			"squat" -> text,
			"bench" -> text,
			"deadlift" -> text,
			"wilks" -> text,
			"date" -> text,
			"context" -> text
		)(RecordData.apply)(RecordData.unapply)
	)
	
	def recordUpdate = DBAction(parse.urlFormEncoded) { implicit request =>
	    println("record update")
	    println(request.body)
	    
	    implicit val session = request.dbSession

	    val playSession = request.session
		playSession.get("connected").map { user =>
		   	val username = playSession.get("username").get
		    val user = Users.getUser(username).getOrElse(User(None,"","","","",""))
		    val recordData = recordForm.bindFromRequest.get
		   	
		   	if(!SSMURecords.belongs(recordData.id.get.toInt, user.id.get)) {
		   		Unauthorized("Unauthorized modification of record. Record does not belong to user")
		   	}

		   	val competition = recordData.context == "competition"
		   	val doubleValues : Either[Tuple5[Double, Double, Double, Double, Double], Result] = try{
		   		Left(
		   			if(recordData.bodyweight == "") 0 else recordData.bodyweight.toDouble,
		   		    if(recordData.squat == "") 0 else recordData.squat.toDouble,
		   		    if(recordData.bench == "") 0 else recordData.bench.toDouble,
		   		    if(recordData.deadlift == "") 0 else recordData.deadlift.toDouble,
		   		    if(recordData.wilks == "") 0 else recordData.wilks.toDouble
		   		)
		   	} catch {
		   		case ex: NumberFormatException => 
		   		    println("invalid input for squat, bench, deadlift, total or wilks")
		   		    Right(BadRequest("invalid input for squat, bench, deadlift, total or wilks"))
		   		case ex: Exception =>
		   		    ex.printStackTrace()
		   		    Right(BadRequest("Unknown internal server error"))
		   	}
		   	
		   	if(doubleValues.isRight) {
		   	    doubleValues.right.get
		   	} else {
		   	    // TODO
		   	    val (bodyweight, squat, bench, deadlift, wilks) = if(recordData.unit == "lbs") {
		   	    	val (bodyweight, squat, bench, deadlift, wilks) = doubleValues.left.get
		   	    	(
		   	    	        SSMURecords.lbsToKg(bodyweight),
		   	    	        SSMURecords.lbsToKg(squat),
		   	    	        SSMURecords.lbsToKg(bench),
		   	    	        SSMURecords.lbsToKg(deadlift),
		   	    	        SSMURecords.lbsToKg(wilks)
		   	    	)
		   	    } else doubleValues.left.get

		   	    val fWilks = if(wilks == 0 && bodyweight != 0) SSMURecords.calculateWilks(squat, bench, deadlift, bodyweight, recordData.sex == "male") else wilks
		   	    val total = squat + bench + deadlift

		   		SSMURecords.updateRecord(recordData.id.get.toInt,
		   	        recordData.weightClass,
		   	        recordData.sex,
		   	        bodyweight,
		   	        squat,
		   	        bench,
		   	        deadlift,
		   	        total,
		   	        fWilks,
		   	        recordData.date,
		   	        competition)
		   	
		   	    Redirect(routes.Settings.settings)
		   	}
	    }.getOrElse{
			Unauthorized("Oops, you are not connected")
	    }
	}
	
	def recordNew = DBAction(parse.urlFormEncoded){ implicit request =>
	    println("new record")
	    println(request.body)
	    Ok("test")

	    implicit val session = request.dbSession

	    val playSession = request.session
		playSession.get("connected").map { user =>
		   	val username = playSession.get("username").get
		    val user = Users.getUser(username).getOrElse(User(None,"","","","",""))
		    val recordData = recordForm.bindFromRequest.get

		   	val competition = recordData.context == "competition"
		   	val doubleValues : Either[Tuple5[Double, Double, Double, Double, Double], Result] = try{
		   		Left(
		   			if(recordData.bodyweight == "") 0 else recordData.bodyweight.toDouble,
		   		    if(recordData.squat == "") 0 else recordData.squat.toDouble,
		   		    if(recordData.bench == "") 0 else recordData.bench.toDouble,
		   		    if(recordData.deadlift == "") 0 else recordData.deadlift.toDouble,
		   		    if(recordData.wilks == "") 0 else recordData.wilks.toDouble
		   		)
		   	} catch {
		   		case ex: NumberFormatException => 
		   		    println("invalid input for squat, bench, deadlift, total or wilks")
		   		    Right(BadRequest("invalid input for squat, bench, deadlift, total or wilks"))
		   		case ex: Exception =>
		   		    ex.printStackTrace()
		   		    Right(BadRequest("Unknown internal server error"))
		   	}
		   	
		   	if(doubleValues.isRight) {
		   	    doubleValues.right.get
		   	} else {
		   	    // TODO
		   	    val (bodyweight, squat, bench, deadlift, wilks) = if(recordData.unit == "lbs") {
		   	    	val (bodyweight, squat, bench, deadlift, wilks) = doubleValues.left.get
		   	    	(
		   	    	        SSMURecords.lbsToKg(bodyweight),
		   	    	        SSMURecords.lbsToKg(squat),
		   	    	        SSMURecords.lbsToKg(bench),
		   	    	        SSMURecords.lbsToKg(deadlift),
		   	    	        SSMURecords.lbsToKg(wilks)
		   	    	)
		   	    } else doubleValues.left.get
		   	    val fWilks = if(wilks == 0 && bodyweight != 0) SSMURecords.calculateWilks(squat, bench, deadlift, bodyweight, recordData.sex == "male") else wilks
		   	    val total = squat + bench + deadlift
		   	    val name = if(user.firstname == "") user.username else user.firstname + " " + user.lastname
		   	    SSMURecords.create(user.id.get, name, recordData.weightClass, recordData.sex, bodyweight, squat, bench, deadlift, total, fWilks, recordData.date, competition)
		   	
		   	    Redirect(routes.Settings.settings)
		   	}
	    }.getOrElse{
			Unauthorized("Oops, you are not connected")
	    }
	}
	
	def recordDelete = DBAction(parse.urlFormEncoded){ implicit request =>
	    implicit val session = request.dbSession

	    val playSession = request.session
		playSession.get("connected").map { user =>
		   	val username = playSession.get("username").get
		    val user = Users.getUser(username).getOrElse(User(None,"","","","",""))
		    val values = request.body.map{ case (name, value) => (name, value.mkString(""))}
		   	
		   	if(!SSMURecords.belongs(values("id").toInt, user.id.get)) {
		   		Unauthorized("Unauthorized modification of record. Record does not belong to user")
		   	} else {
		   	    SSMURecords.delete(values("id").toInt)
		   	}

		   	Redirect(routes.Settings.settings)
	    }.getOrElse{
			Unauthorized("Oops, you are not connected")
	    }
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
		    val values = request.body.map{ case (name, value) => (name, value.mkString(""))}
		    
		    values("email") match {
		    	case "" =>
		    		println("empty email; do nothing")
		    	case email =>
		    	    Users.updateEmail(user.id, email)
		    		//val t = (for{u <- Users.users if u.id === user.id } yield u.email)
		    		//println(t.invoker.list)
		   	}
		    values("username") match {
		    	case "" =>
		    		println("empty username; do nothing")
		    	case newUsername =>
		    	    Users.updateUsername(user.id, newUsername)
		    		//val t = (for{u <- Users.users if u.id === user.id } yield u.username)
		    		//println(t.invoker.list)
		   	}
		    values("firstname") match {
		    	case "" =>
		    		println("empty firstname; do nothing")
		    	case firstname =>
		    	    Users.updateFirstname(user.id, firstname)
		    		//val t = (for{u <- Users.users if u.id === user.id } yield u.firstname)
		    		//println(t.invoker.list)
		   	}
		    values("lastname") match {
		    	case "" =>
		    		println("empty lastname; do nothing")
		    	case lastname =>
		    	    Users.updateLastname(user.id, lastname)
		    		//val t = (for{u <- Users.users if u.id === user.id } yield u.lastname)
		    		//println(t.invoker.list)
		   	}
		    values("password") match {
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