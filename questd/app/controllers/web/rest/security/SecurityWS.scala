package controllers.web.rest.security

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.mvc.Security._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain._
import scala.concurrent._
import scala.concurrent.duration._
import models.domain.user._

trait SecurityWS extends Controller {

  private val SessionIdKey = "sessionid"

  // Store Auth Info
  def storeAuthInfoInResult(result: SimpleResult, loginResult: AuthAPI.LoginResult) = {
    result.withSession(SessionIdKey -> loginResult.session.toString)
  }

  // Configure Authorized check 
  class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

  object Authenticated extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
      request.session.get(SessionIdKey) match { 
        
        case Some(userid: String) => {
          Future {
          
            // Accessing api to auth user.
            // TODO implement db auth here.
            
          
            User(userid + "peputkin in the object", "")
          
          }.map {newUsername =>
            Await.result(
                block(new AuthenticatedRequest(newUsername, request)),
                0 nanos)	
          }
        }

        case None => Future.successful(Unauthorized)
      }
    }
  }

}
