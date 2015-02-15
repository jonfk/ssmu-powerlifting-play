package controllers

import play.api._
import play.api.data.Form
import play.api.mvc._
import play.api.db._
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DBAction
import play.api.data.Forms._
import models.{NewsItems, Users, User}
import actions.AuthenticatedActions._

object Content extends Controller {

    def newsEditor = Authenticated(optionalLogin = false) { implicit request =>
        request.user match {
            case Some(user) =>
                if(user.power == "admin") {
                    Ok(views.html.newsEdit(Some(user))).withSession(request.session)
                } else {
                    Unauthorized("Sorry you don't have the power to publish news.")
                }
            case None =>
                InternalServerError("Something went wrong with user authentication")
        }
    }

    val newsForm = Form(
        tuple(
            "title" -> text,
            "content" -> text
        )
    )

    def newNews = AuthenticatedWithDBAction(optionalLogin = false) { implicit request =>
        println("Received new News: \n" + request.body)

        request.user match {
            case Some(user) =>
                if(user.power == "admin") {
                    implicit val session = request.dbSession
                    val (title, content) = newsForm.bindFromRequest.get
                    NewsItems.submitNews(user, title, content)

                    Redirect(routes.Content.newsEditor)
                } else {
                    Unauthorized("Sorry you don't have the power to publish news.")
                }
            case None =>
                InternalServerError("Something went wrong with user authentication")
        }
    }

}