package controllers.web.rest.component

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.libs.json.JsError
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain._
import controllers.domain.user._
import controllers.web.rest.component.helpers._
import controllers.domain.libs.facebook.UserFB
import com.restfb.exception._
import components._
import controllers.web.rest.protocol._


trait LoginWSImpl extends QuestController with SecurityWSImpl { this: FBAccessor with APIAccessor =>

  
  /**
   * Logins with Facebook or create new user if it not exists
   * 
   * HTTP statuses:
   * 200 - Logged in
   * 401 - Session expired or other problems with facebook login.
   * 500 - Internal error.
   * 503 - Unable to connect to facebook to check status.
   */
  def loginfb = Action.async(parse.json) { implicit request =>

    
      request.body.validate[WSLoginFBRequest].map {
        case params => {
          params.head match {
            case ("token", token: String) => {
              Future {
                try {
                  (Option(fb.fetchObject(token, "me", classOf[UserFB])), None)
                } catch {
                  case ex: FacebookOAuthException => {
                    Logger.debug("Facebook auth failed")
                    (None, Some(Unauthorized(
                        Json.write(WSUnauthorisedResult(UnauthorisedReason.InvalidFBToken))
                        ).as(JSON)))
                  }
                  case ex: FacebookNetworkException => {
                    Logger.debug("Unable to connect to facebook")
                    (None, Some(ServiceUnavailable("Unable to connect to Facebook")))
                  }
                }
              } map { rv =>
                rv match {
                  case (Some(user: UserFB), _) => {
                    val params = LoginFBRequest(user.getId())

                    api.loginfb(params) match {
                      case OkApiResult(Some(loginResult: LoginFBResult)) =>
                        storeAuthInfoInResult(Ok(Json.write(WSLoginFBResult(loginResult.session.toString))).as(JSON), loginResult)
                        
                      case _ => ServerError
                    }

                  }
                  case (None, Some(r: SimpleResult)) => r 
                  case (None, None) => ServerError
                }
              }
            }

            case badRequest => Future.successful { BadRequest("Detected error:" + badRequest.toString + " is not valid request") }
          }

        }

      }.recoverTotal {
        e => Future.successful { BadRequest("Detected error:" + JsError.toFlatJson(e)) }
      }

    }

}