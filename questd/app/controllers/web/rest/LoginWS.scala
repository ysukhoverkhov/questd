package controllers.web.rest

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain._
import controllers.web.rest.security._
import controllers.web.rest.helpers._
import scala.annotation.varargs

import com.restfb.exception._

// EXAMPLE
/*
  /*
   * LoginParams
   */
  implicit val loginParamReads = (
    (__ \ 'name).read[String] and
    (__ \ 'pass).read[String])(LoginParams)

  /*
     * LoginResult
     */
  implicit val loginResultWrites = new Writes[LoginResult] {
    def writes(c: LoginResult): JsValue = {
      Json.obj(
        "result" -> c.result.id,
        "session" -> c.session.toString)
    }
  }
*/


// TODO CRITICAL Write tests for WS with mock API.
object LoginWS extends QuestController with SecurityWS {
  

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
    Future.successful(Ok(""))

    /*
      request.body.validate[Map[String, String]].map {
        case params => {
          params.head match {
            case ("token", token: String) => {
              Future {
                try {
                  (Option(FacebookClient(token).fetchObject("me", classOf[UserFB])), None)
                } catch {
                  case ex: FacebookOAuthException => {
                    Logger.debug("Facebook auth failed")
                    (None, Some(Unauthorized("Facebook session expired")))
                  }
                  case ex: FacebookNetworkException => {
                    Logger.debug("Unable to connect to facebook")
                    (None, Some(ServiceUnavailable("Unable to connect to Facebook")))
                  }
                }
              } map { rv =>
                rv match {
                  case (Some(user: UserFB), _) => {
                    val params = AuthAPI.LoginFBParams(user.getId())

                    AuthAPI.loginfb(params) match {
                      case OkApiResult(Some(loginResult: AuthAPI.LoginFBResult)) =>
                        storeAuthInfoInResult(Ok(loginResult.session.toString), loginResult)
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
    * 
    */
  }

}

