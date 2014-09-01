package controllers.web.admin.component

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
import controllers.domain.app.user._
import components._
import controllers.web.rest.protocol._

object SecurityAdminImpl {
  // Constant for session name in cookie
  val SessionIdKey = "sessionid"
  val SessionValue = "UOacUhyfArYuO0leG8oI"
}

trait SecurityAdminImpl extends InternalErrorLogger {

  // Store Auth Info
  def storeAuthInfoInResult(result: SimpleResult) = {
    result.withSession(SecurityAdminImpl.SessionIdKey -> SecurityAdminImpl.SessionValue)
  }

  // Configure Authorized check 
  class AuthenticatedRequest[A](request: Request[A]) extends WrappedRequest[A](request)

  object Authenticated extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
      request.session.get(SecurityAdminImpl.SessionIdKey) match {

        case Some(sessionid: String) if (sessionid == SecurityAdminImpl.SessionValue) => 
          block(new AuthenticatedRequest(request))
          
        case _ =>
         Future.successful(Unauthorized("Unauthorized"))
      }
    }

  }

}
