package controllers.web.rest.security

import scala.language.postfixOps
import scala.concurrent._
import scala.concurrent.duration._

import play.api._
import play.api.mvc._
import play.api.mvc.Security._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain._
import controllers.domain._
import models.domain.user._

trait SecurityWS extends Controller {

  private val SessionIdKey = "sessionid"

  // Store Auth Info
  def storeAuthInfoInResult(result: SimpleResult, loginResult: AuthAPI.LoginFBResult) = {
    result.withSession(SessionIdKey -> loginResult.session.toString)
  }

  // Configure Authorized check 
  class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

  object Authenticated extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
      request.session.get(SessionIdKey) match { 
        
        case Some(sessionid: String) => {
          Future {
          
            val params = AuthAPI.UserParams(sessionid)
            
            AuthAPI.user(params) match {
              case OkApiResult(body) => body match {
                case Some(result: AuthAPI.UserResult) =>  result.user
                case None => InternalServerError
              }
              
              case NotAuthorisedApiResult(body) => {
                Unauthorized
              }
              
              case InternalErrorApiResult(body) => {
                InternalServerError
              }
            }
          
          }.map {newUser => newUser match {
              case user: User => 
	            Await.result(
	              block(new AuthenticatedRequest(user, request)),
	              0 nanos)
              case er: Status => er  
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
