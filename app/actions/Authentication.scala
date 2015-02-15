package actions

import models.{Users, User}
import play.api.db.slick.DBAction
import play.api.db.slick
import play.api.mvc._
import play.api.mvc.Results._

case class AuthenticatedRequest[A](
                                      val user: Option[User], request: Request[A]
                                      ) extends WrappedRequest(request)

case class AuthenticatedRequestWithDBSession[A](
                                      val user: Option[User], request: Request[A], dbSession : slick.Session
                                      ) extends WrappedRequest(request)

object AuthenticatedActions {

    def Authenticated[A](p: BodyParser[A])(optionalLogin: Boolean)(f: AuthenticatedRequest[A] => Result) = {
        Action(p) { request =>
            request.session.get("connected").map { email =>
                val username = request.session.get("username").get
                Users.getUser(username) match {
                    case Some(user) =>
                        f(AuthenticatedRequest(Some(user), request))
                    case None =>
                        InternalServerError(s"Authenticated User : $username not found")
                }
            }.getOrElse {
                if (optionalLogin) {
                    f(AuthenticatedRequest(None, request))
                } else {
                    Unauthorized("Oops, you are not Signed in")
                }
            }
        }
    }

    def AuthenticatedWithDBAction[A](p: BodyParser[A])(optionalLogin: Boolean)(f: AuthenticatedRequestWithDBSession[A] => Result): Action[A] = {
        DBAction(p) { request =>
            request.session.get("connected").map { email =>
                val username = request.session.get("username").get
                Users.getUser(username) match {
                    case Some(user) =>
                        f(AuthenticatedRequestWithDBSession(Some(user), request, request.dbSession))
                    case None =>
                        InternalServerError(s"Authenticated User : $username not found")
                }
            }.getOrElse {
                if (optionalLogin) {
                    f(AuthenticatedRequestWithDBSession(None, request, request.dbSession))
                } else {
                    Unauthorized("Oops, you are not Signed in")
                }
            }
        }
    }

    // Overloaded method to use the default body parser
    import play.api.mvc.BodyParsers._
    def Authenticated(optionalLogin: Boolean)(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent]  = {
        Authenticated(parse.anyContent)(optionalLogin)(f)
    }

    def AuthenticatedWithDBAction(optionalLogin : Boolean)(f: AuthenticatedRequestWithDBSession[AnyContent] => Result): Action[AnyContent] = {
        AuthenticatedWithDBAction(parse.anyContent)(optionalLogin)(f)
    }

}
