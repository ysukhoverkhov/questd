package controllers.web.rest

import com.restfb._
import com.restfb.types._
import com.restfb.exception._

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain._
import controllers.web.rest.security._

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

object LoginWS extends Controller with SecurityWS {

  /* *****
   * Login FB section
   * *****/

  // TODO: Implement tripple try here for fb requests here to compensate network failures.
  // TODO: Auto Log InternalServerError
  def loginfb = Action.async(parse.json) { implicit request =>
    {
      request.body.validate[Map[String, String]].map {
        case params => {
          params.head match {
            case ("token", token: String) => {
              Future {
                try {
                  val facebookClient = new DefaultFacebookClient(token);
                  Option(facebookClient.fetchObject("me", classOf[User]));
                } catch {
                  case ex: FacebookOAuthException => {
                    Logger.debug("Facebook auth failed")
                    None
                  }
                }
              } map { rv =>
                rv match {
                  case Some(user: User) => {
                    val params = AuthAPI.LoginFBParams(user.getId())

                    AuthAPI.loginfb(params) match {
                      case OkApiResult(Some(loginResult: AuthAPI.LoginFBResult)) => 
                        storeAuthInfoInResult(Ok(loginResult.session.toString), loginResult)
                      case _ => InternalServerError
                    }

                  }
                  case None => Unauthorized("Facebook session expired")
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

}

