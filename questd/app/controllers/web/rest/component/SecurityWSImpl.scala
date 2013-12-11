package controllers.web.rest.component

import scala.language.postfixOps
import scala.concurrent._
import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.mvc.Security._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json._

import models.domain._
import controllers.web.rest.component.helpers._
import controllers.domain._
import controllers.domain.user._
import components._
import controllers.web.rest.protocol._

object SecurityWSImpl {
  // Constant for session name in cookie
  val SessionIdKey = "sessionid"
}

trait SecurityWSImpl extends InternalErrorLogger { this: APIAccessor =>

  // Store Auth Info
  def storeAuthInfoInResult(result: SimpleResult, loginResult: LoginFBResult) = {
    result.withSession(SecurityWSImpl.SessionIdKey -> loginResult.session.toString)
  }

  // Configure Authorized check 
  class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

  object Authenticated extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
      request.session.get(SecurityWSImpl.SessionIdKey) match {

        case Some(sessionid: String) => {
          Future {

            val params = UserRequest(sessionID = Some(sessionid))

            api.getUser(params) match {
              case OkApiResult(body) => body match {
                case Some(result: UserResult) => result.user
                case None => ServerError
              }

              case NotAuthorisedApiResult(body) => {
                Unauthorized(
                  Json.write(WSUnauthorisedResult(UnauthorisedReason.InvalidFBToken))).as(JSON)
              }

              case InternalErrorApiResult(body) => {
                ServerError
              }

              case _ => {
                ServerError
              }
            }

          }.flatMap { newUser =>
            newUser match {
              case user: User => block(new AuthenticatedRequest(user, request))
              case er: Status => Future.successful(er)
              case er: SimpleResult => Future.successful(er)
            }
          }
        }

        case None => {
          Logger.debug("Session not found in cookie, returning Unauthorized")
          Future.successful(Unauthorized)
        }
      }
    }

  }

}
