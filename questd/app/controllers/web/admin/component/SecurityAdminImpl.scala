package controllers.web.admin.component

import controllers.web.helpers._
import play.api.mvc._

import scala.concurrent._
import scala.language.postfixOps

object SecurityAdminImpl {
  // Constant for session name in cookie
  val SessionIdKey = "sessionid"
  val SessionValue = "UOacUhyfArYuO0leG8oI"
}

trait SecurityAdminImpl extends InternalErrorLogger {

  // Store Auth Info
  def storeAuthInfoInResult(result: Result) = {
    result.withSession(SecurityAdminImpl.SessionIdKey -> SecurityAdminImpl.SessionValue)
  }

  // Configure Authorized check
  class AuthenticatedRequest[A](request: Request[A]) extends WrappedRequest[A](request)

  object Authenticated extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]) = {
      request.session.get(SecurityAdminImpl.SessionIdKey) match {

        case Some(sessionId: String) if sessionId == SecurityAdminImpl.SessionValue =>
          block(new AuthenticatedRequest(request))

        case _ =>
         Future.successful(Unauthorized("Unauthorized"))
      }
    }

  }

}
