package controllers

import play.api.mvc.Controller
import actions.AuthenticatedActions._

object Schedule extends Controller {
    def schedule = AuthenticatedWithDBAction(optionalLogin = true) { implicit request =>
        request.user match {
            case Some(user) =>
                Ok(views.html.schedule(Some(user))).withSession(request.session)
            case None =>
                Ok(views.html.schedule(None))
        }
    }
}
