package controllers.web.rest.component

import scala.language.postfixOps
import scala.concurrent._
import scala.concurrent.duration._
import play.api._
import play.api.mvc._
import play.api.mvc.Security._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import models.domain.user._
import controllers.web.rest.component.helpers._
import controllers.domain._

trait SecurityWSImpl extends InternalErrorLogger { this: WSComponent#WS =>

  // Store Auth Info
  def storeAuthInfoInResult(result: SimpleResult, loginResult: LoginFBResult) = {
    result.withSession(SessionIdKey -> loginResult.session.toString)
  }

  // Configure Authorized check 
  class AuthenticatedRequest[A](val user: User, request: Request[A]) extends WrappedRequest[A](request)

  object Authenticated extends ActionBuilder[AuthenticatedRequest] {
    def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[SimpleResult]) = {
      request.session.get(SessionIdKey) match { 
        
        case Some(sessionid: String) => {
          Future {
          
            val params = UserParams(sessionid)
            
            api.user(params) match {
              case OkApiResult(body) => body match {
                case Some(result: UserResult) =>  result.user
                case None => ServerError
              }
              
              case NotAuthorisedApiResult(body) => {
                Unauthorized
              }
              
              case InternalErrorApiResult(body) => {
                ServerError
              }

              case _ => {
                ServerError
              }
            }
          
          }.flatMap {newUser => newUser match {
              case user: User => block(new AuthenticatedRequest(user, request))
              case er: Status => Future.successful(er)  
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
