package controllers.web.rest.component

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.libs.json.JsError
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain._
import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.domain.libs.facebook.UserFB
import com.restfb.exception._
import components._
import controllers.web.rest.protocol._
import org.json4s.MappingException
import controllers.web.rest.config.WSConfigHolder

trait LoginWSImpl extends QuestController with SecurityWSImpl { this: FBAccessor with APIAccessor with WSConfigHolder =>

  /**
   * Logins with Facebook or create new user if it not exists
   *
   * HTTP statuses:
   * 200 - Logged in
   * 401 - Session expired or other problems with facebook login.
   * 500 - Internal error.
   * 503 - Unable to connect to facebook to check status.
   */
  def loginfb = Action.async { implicit request =>

    request.body.asJson.fold {
      Future.successful { BadRequest("Detected error: Empty request") }
    } { js =>

      Future {

        try {
          val loginRequest = Json.read[WSLoginFBRequest](js.toString)

          // Check app version.
          if (config.values(ConfigParams.MinAppVersion).toInt > loginRequest.appVersion) {
            (None, Some(Unauthorized(
              Json.write(WSUnauthorisedResult(UnauthorisedReason.UnsupportedAppVersion))).as(JSON)))
          } else {
            
            // Login facebook.
            try {
              (Option(fb.fetchObject(loginRequest.token, "me", classOf[UserFB])), None)
            } catch {
              case ex: FacebookOAuthException => {
                Logger.debug("Facebook auth failed")
                (None, Some(Unauthorized(
                  Json.write(WSUnauthorisedResult(UnauthorisedReason.InvalidFBToken))).as(JSON)))
              }
              case ex: FacebookNetworkException => {
                Logger.debug("Unable to connect to facebook")
                (None, Some(ServiceUnavailable("Unable to connect to Facebook")))
              }
            }
          }
        } catch {
          case ex @ (_: MappingException | _: org.json4s.ParserUtil$ParseException) => {
            (None, Some(BadRequest(ex.getMessage())))
          }
          case ex: Throwable => {
            Logger.error("Api calling exception", ex)
            (None, Some(ServerError))
          }
        }
      } map { rv =>
        rv match {
          case (Some(user: UserFB), _) => {
            val params = LoginFBRequest(user)

            api.loginfb(params) match {
              case OkApiResult(Some(loginResult: LoginFBResult)) =>
                storeAuthInfoInResult(Ok(Json.write(WSLoginFBResult(loginResult.session))).as(JSON), loginResult.session)

              case _ => ServerError
            }

          }
          case (None, Some(r: SimpleResult)) => r
          case (None, None) => ServerError
        }
      }
    }
  }

}
