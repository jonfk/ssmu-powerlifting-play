package controllers

import play.api._
import play.api.mvc._
import models.SSMURecord
import play.api.db._
import models.SSMURecords
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import models.SSMUProfiles
import models.Users
import models.User
import models.SSMUProfile
import actions.AuthenticatedActions._

object Profiles extends Controller {
    def profile(username: String) = AuthenticatedWithDBAction(optionalLogin = true) { implicit request =>
        implicit val session = request.dbSession

        val profileValues : Either[Tuple4[User, SSMUProfile, List[SSMURecord], List[SSMURecord]], Result]= (for{u <- Users.users if u.username === username} yield u).list match {
        	case Nil =>
        		Right(NotFound("User "+username+" does not exist"))
        	case user::l =>
        		if(SSMUProfiles.exists(user.id.get)) {
        			val profile = SSMUProfiles.getProfile(user.id.get)
        			val meetRecords = (for{ r <- SSMURecords.records if r.userId === user.id && r.competition} yield r).list
        			val trainingRecords = (for{ r <- SSMURecords.records if r.userId === user.id && !r.competition} yield r).list
        			Left((user, profile, meetRecords, trainingRecords))
        		} else {
        			Right(Ok("User " + username + " does not have a profile"))
        		}
        }

       request.user match {
           case Some(userLoggedIn) =>
               profileValues match {
                   case Right(r) =>
                       r
                   case Left((user, profile, meetRecords, trainingRecords)) =>
                       Ok(views.html.profile(user, profile, meetRecords, trainingRecords, userLoggedIn=Some(userLoggedIn))).withSession(request.session)
               }
           case None =>
               profileValues match {
                   case Right(r) =>
                       r
                   case Left((user, profile, meetRecords, trainingRecords)) =>
                       Ok(views.html.profile(user, profile, meetRecords, trainingRecords))
               }
       }
    }
}