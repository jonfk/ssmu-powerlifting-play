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

    def newsEditor(itemId : Option[Int]) = AuthenticatedWithDBAction(optionalLogin = false) { implicit request =>
        implicit val session = request.dbSession
        request.user match {
            case Some(user) =>
                if(user.power == "admin") {
                    val userItems = (for{item <- NewsItems.news if item.userId === user.id}yield item).list
                    itemId match {
                        case Some(itemId) =>
                            val item = (for{item <- NewsItems.news if item.id === itemId && item.userId === user.id} yield item).list
                            if (item.nonEmpty) {
                                Ok(views.html.newsEdit(userItems, Some(item(0)), Some(user))).withSession(request.session)
                            } else {
                                NotFound(s"News Item with id: $itemId not found")
                            }
                        case None =>
                            Ok(views.html.newsEdit(userItems, None, Some(user))).withSession(request.session)
                    }
                } else {
                    Unauthorized("Sorry you don't have the power to publish news.")
                }
            case None =>
                InternalServerError("Something went wrong with user authentication")
        }
    }

    val newsForm = Form(
        tuple(
            "id" -> optional(number),
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
                    val (id, title, content) = newsForm.bindFromRequest.get
                    NewsItems.submitNews(user, title, content)

                    Redirect(routes.Content.newsEditor(None))
                } else {
                    Unauthorized("Sorry you don't have the power to publish news.")
                }
            case None =>
                InternalServerError("Something went wrong with user authentication")
        }
    }

    def deleteNewsItem(id: Option[Int]) = AuthenticatedWithDBAction(optionalLogin = false) { implicit request =>
        request.user match {
            case Some(user) =>
                if(user.power == "admin") {
                    if(id != None) {
                        val tid = id.get
                        implicit val session = request.dbSession
                        val itemToBeDeleted = (for{item <- NewsItems.news if item.id === tid}yield item).list
                        if(itemToBeDeleted.nonEmpty) {
                            if(itemToBeDeleted(0).userId == user.id.get) {
                                // delete item
                                NewsItems.deleteNews(tid)
                            } else {
                                Unauthorized("Sorry you don't have the power to delete this news Article")
                            }
                        } else {
                            NotFound(s"Not found news item with id: $tid")
                        }
                    }

                    Redirect(routes.Content.newsEditor(None))
                } else {
                    Unauthorized("Sorry you don't have the power to publish news.")
                }
            case None =>
                InternalServerError("Something went wrong with user authentication")
        }
    }

}