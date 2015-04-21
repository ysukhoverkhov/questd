package controllers.web.rest.component

import scala.concurrent.Future
import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain._
import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import components._
import org.json4s.MappingException
import controllers.web.rest.config.WSConfigHolder
import controllers.sn.exception._

private [component] object LoginWSImplTypes {

  /**
   * Payload in case of 401 error.
   */
  case class WSUnauthorisedResult(code: UnauthorisedReason.Value)

  /**
   *  Reasons of Unauthorised results.
   */
  object UnauthorisedReason extends Enumeration {

    /**
     *  FB tells us it doesn't know the token.
     */
    val InvalidFBToken = Value

    /**
     *  Supplied session is not valid on our server.
     */
    val SessionNotFound = Value

    /**
     * Passed version of the application is not supported.
     */
    val UnsupportedAppVersion = Value
  }

  /**
   * Login Request
   */
  case class WSLoginRequest(snName:String, token: String, appVersion: Int)

  /**
   * Login Result
   */
  case class WSLoginResult(sessionId: String, userId: String)

}


trait LoginWSImpl extends QuestController with SecurityWSImpl { this: SNAccessor with APIAccessor with WSConfigHolder =>

  import LoginWSImplTypes._

  /**
   * Logins with Facebook or create new user if it not exists
   *
   * HTTP statuses:
   * 200 - Logged in
   * 400 - Incorrect sovial network name used.
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
          val loginRequest = Json.read[WSLoginRequest](js.toString())

          // Check app version.
          if (config.values(ConfigParams.MinAppVersion).toInt > loginRequest.appVersion) {
            Right(Unauthorized(
              Json.write(WSUnauthorisedResult(UnauthorisedReason.UnsupportedAppVersion))).as(JSON))
          } else {

            // Login with SN.
            try {
              Left(LoginRequest(
                loginRequest.snName,
                sn.clientForName(loginRequest.snName).fetchUserByToken(loginRequest.token)))
            } catch {
              case ex: AuthException =>
                Logger.debug("Facebook auth failed")
                Right(Unauthorized(
                  Json.write(WSUnauthorisedResult(UnauthorisedReason.InvalidFBToken))).as(JSON))
              case ex: NetworkException =>
                Logger.debug("Unable to connect to facebook")
                Right(ServiceUnavailable("Unable to connect to Facebook"))
              case ex: SocialNetworkClientNotFound =>
                Logger.debug("Request to unexisting social network.")
                Right(BadRequest("Social network with provided name not found"))
            }
          } : Either[LoginRequest, Result]
        } catch {
          case ex @ (_: MappingException | _: org.json4s.ParserUtil$ParseException) =>
            Right(BadRequest(ex.getMessage))
          case ex: Throwable =>
            Logger.error("Api calling exception", ex)
            Right(ServerError)
        }
      } map {
        case Left(params) =>
          api.login(params) match {
            case OkApiResult(loginResult: LoginResult) =>
              storeAuthInfoInResult(Ok(Json.write(WSLoginResult(loginResult.sessionId, loginResult.userId))).as(JSON), loginResult.sessionId)

            case _ => ServerError
          }
        case Right(r: Result) => r
      }
    }
  }
}
