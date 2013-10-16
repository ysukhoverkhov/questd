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

  def login = Action {
    Ok(views.html.testclient.login(loginCredentialsForm))
  }

  def loginTarget = Action { implicit request =>
    loginCredentialsForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.testclient.login(formWithErrors))
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

