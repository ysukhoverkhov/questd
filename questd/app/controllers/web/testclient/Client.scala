package controllers.web.testclient


import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

import play.api.libs.ws._
import play.api.libs.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future


object Client extends Controller {

  def index = Action {
    Redirect(routes.Client.login)
  }

  case class LoginCredentials(name: String, pass: String)
  val loginCredentialsForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "pass" -> nonEmptyText)(LoginCredentials.apply)(LoginCredentials.unapply))

  case class RegisterCredentials(name: String, pass1: String, pass2: String)
  val registerCredentialsForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "pass1" -> nonEmptyText,
      "pass2" -> nonEmptyText)(RegisterCredentials.apply)(RegisterCredentials.unapply) verifying ("Passwords are not the same", (x) => x.pass1 == x.pass2))

  def login = Action {
    Ok(views.html.testclient.login(loginCredentialsForm, registerCredentialsForm))
  }

  def loginTarget = Action.async { implicit request =>
    loginCredentialsForm.bindFromRequest.fold(
        
      formWithErrors => {
        scala.concurrent.Future {
        	BadRequest(views.html.testclient.login(formWithErrors, registerCredentialsForm))
        }
      },
      
      loginCreds => {
        val data = Json.obj(
          "name" -> loginCreds.name,
          "pass" -> loginCreds.pass)

        WS.url(controllers.web.rest.routes.LoginAPI.login.absoluteURL(false))
          .post(data)
          .map(result => {
            Ok(result.body)
        })
      }
    )
  }

  def registerTarget = Action.async { implicit request =>
    registerCredentialsForm.bindFromRequest.fold(
      formWithErrors => {
        scala.concurrent.Future {
          BadRequest(views.html.testclient.login(loginCredentialsForm, formWithErrors))
        }
      },

      registerCreds => {
        val data = Json.obj(
          "name" -> registerCreds.name,
          "pass" -> registerCreds.pass1)

        WS.url(controllers.web.rest.routes.LoginAPI.register.absoluteURL(false))
          .post(data)
          .map(result => {
            Ok(result.body)
        })
      }
     )

  }

}

