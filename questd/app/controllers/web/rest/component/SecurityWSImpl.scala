package controllers.web.rest.component

import components._
import controllers.domain._
import controllers.domain.app.user._
import controllers.web.helpers.{InternalErrorLogger, _}
import models.domain.user.User
import play.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent._
import scala.language.postfixOps

private object SecurityWSImplTypes {
  // Constant for session name in cookie
  val SessionIdKey = "sessionid"
}

trait SecurityWSImpl extends InternalErrorLogger { this: APIAccessor =>

  import controllers.web.rest.component.LoginWSImplTypes._
  import SecurityWSImplTypes._

  // Store Auth Info
  def storeAuthInfoInResult(result: Result, session: String) = {
    result.withSession(SessionIdKey -> session)
  }

  // Configure Authorized check
  class AuthenticatedRequest[A](val user: User, val request: Request[A]) extends WrappedRequest[A](request)

  object Authenticated extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]) = {
      request.session.get(SessionIdKey) match {

        case Some(sessionid: String) =>
          Future {

            api.getUser(GetUserRequest(sessionId = Some(sessionid))) match {
              case OkApiResult(GetUserResult(UserResultCode.OK, Some(user))) => user

              case OkApiResult(GetUserResult(UserResultCode.NotFound, _)) =>
                Unauthorized(
                  Json.write(WSUnauthorisedResult(UnauthorisedReason.SessionNotFound))).as(JSON)

              case InternalErrorApiResult(ex) =>
                Logger.error("InternalErrorApiResult", ex)
                ServerError

              case a =>
                Logger.error(s"Unexpected result - $a")
                ServerError
            }

          }.flatMap {
            case user: User => block(new AuthenticatedRequest(user, request))
            case er: Status => Future.successful(er)
            case er: Result => Future.successful(er)
          }

        case None =>
          Logger.debug("Session not found in cookie, returning Unauthorized")
          Future.successful(Unauthorized)
      }
    }
  }
}
