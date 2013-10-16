package controllers.web.testclient

import play.api._
import play.api.mvc._

import play.api.data._
import play.api.data.Forms._

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
      "pass2" -> nonEmptyText)(RegisterCredentials.apply)(RegisterCredentials.unapply) verifying("Passwords are not the same", (x) => x.pass1 == x.pass2)
      )
      
  def login = Action {
    Ok(views.html.testclient.login(loginCredentialsForm, registerCredentialsForm))
  }

  def loginTarget = Action { implicit request =>
    loginCredentialsForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.testclient.login(formWithErrors, registerCredentialsForm))
      },
      userData => {
        //    val newUser = models.User(userData.name, userData.age)
        //    val id = models.User.create(newUser)
        //    Redirect(routes.Application.home(id))
        Ok
      })

  }

  def registerTarget = Action { implicit request =>
    registerCredentialsForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.testclient.login(loginCredentialsForm, formWithErrors))
      },
      userData => {
        //    val newUser = models.User(userData.name, userData.age)
        //    val id = models.User.create(newUser)
        //    Redirect(routes.Application.home(id))
        Ok
      })

  }
  
  
  def main = Action {
    Ok("Main client screen")
  }

}

