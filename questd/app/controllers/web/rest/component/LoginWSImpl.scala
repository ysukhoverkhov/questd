package controllers.web.rest.component

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.libs.json.JsError
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain._
import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import components._
import controllers.web.rest.protocol._
import org.json4s.MappingException
import controllers.web.rest.config.WSConfigHolder
import controllers.sn.client.SNUser
import controllers.sn.exception._

trait LoginWSImpl extends QuestController with SecurityWSImpl { this: SNAccessor with APIAccessor with WSConfigHolder =>

  /**
   * Logins with Facebook or create new user if it not exists
   *
   * HTTP statuses:
   * 200 - Logged in
   * 401 - Session expired or other problems with facebook login.
   * 500 - Internal error.
   * 503 - Unable to connect to facebook to check status.
   */
  def login = Action.async { implicit request =>

    request.body.asJson.fold {
      Future.successful { BadRequest("Detected error: Empty request") }
    } { js =>

      Future {

        try {
          val loginRequest = Json.read[WSLoginRequest](js.toString)

          // Check app version.
          if (config.values(ConfigParams.MinAppVersion).toInt > loginRequest.appVersion) {
            (None, Some(Unauthorized(
              Json.write(WSUnauthorisedResult(UnauthorisedReason.UnsupportedAppVersion))).as(JSON)))
          } else {

            // Login with SN.
            // TODO: refactor here with either.
            try {
              (
                Option(LoginRequest(
                  loginRequest.snName,
                  sn.clientForName(loginRequest.snName).fetchUserByToken(loginRequest.token))),
                None)
            } catch {
              case ex: AuthException => {
                Logger.debug("Facebook auth failed")
                (None, Some(Unauthorized(
                  Json.write(WSUnauthorisedResult(UnauthorisedReason.InvalidFBToken))).as(JSON)))
              }
              case ex: NetworkException => {
                Logger.debug("Unable to connect to facebook")
                (None, Some(ServiceUnavailable("Unable to connect to Facebook")))
              }
              case ex: SocialNetworkClientNotFound => {
                // TODO: test me.
                Logger.debug("Request to unexisting social network.")
                (None, Some(BadRequest("Social network with provided name not found")))
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
          case (Some(params), _) => {
            api.login(params) match {
              case OkApiResult(loginResult: LoginResult) =>
                storeAuthInfoInResult(Ok(Json.write(WSLoginResult(loginResult.session.toString))).as(JSON), loginResult)

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
