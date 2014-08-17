package controllers.web.testclient

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
// TODO: remove client login completelly.
object Client extends Controller {

  def index = Action {
    Redirect(routes.Client.loginfb)
  }
  
  /*
  /*
   * Form definitions
   */
  case class LoginCredentials(name: String, pass: String)
  val loginCredentialsForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "pass" -> nonEmptyText)(LoginCredentials.apply)(LoginCredentials.unapply))
*/

  case class FBCredentials(token: String)
  val fbCredentialsForm = Form(
    mapping(
      "token" -> nonEmptyText)(FBCredentials.apply)(FBCredentials.unapply))

  def loginfb = Action {
    Ok(views.html.testclient.loginfb(fbCredentialsForm))
  }

  /**
   * Login facebook
   */
  def loginfbTarget = Action.async { implicit request =>
    fbCredentialsForm.bindFromRequest.fold(

      formWithErrors => {
        scala.concurrent.Future {
          BadRequest(views.html.testclient.loginfb(formWithErrors))
        }
      },

      loginCreds => {
        val data = Json.obj(
          "token" -> loginCreds.token)

        WS.url(controllers.web.rest.routes.LoginWS.login.absoluteURL(false))
          .post(data)
          .map(result => {
            result.header(SET_COOKIE) match {
              case Some(c: String) => Ok(result.body + " " + result.status.toString).withHeaders(SET_COOKIE -> c)
              case _ => Ok(result.body + "<br>" + result.status.toString)
            }

          })
      })
  }

}

