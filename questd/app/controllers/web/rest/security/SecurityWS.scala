package controllers.web.rest.security

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.mvc.Security._
import controllers.domain._

trait SecurityWS extends Controller {

  private val SessionIdKey = "sessionid"
  
  
  // Store Auth Info
  def storeAuthInfoInResult(result: SimpleResult, loginResult: AuthAPI.LoginResult) = {
    result.withSession(SessionIdKey -> loginResult.session.toString)
  }
  
  
  // Configure Authenticated check 
  def username(request: RequestHeader) = request.session.get(SessionIdKey)
  def onUnauthorized(request: RequestHeader) = Unauthorized
 
  def isAuthenticated(f: => String => Request[AnyContent] => SimpleResult) = {
    Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }

  def isAuthenticatedAsync(f: => String => Request[AnyContent] => Future[SimpleResult]) = {
    Authenticated(username, onUnauthorized) { user =>
      Action.async(request => f(user)(request))
    }
  }


  
  // Configure Authorized check 
  
  
  
}
