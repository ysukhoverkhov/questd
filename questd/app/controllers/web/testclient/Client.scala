package controllers.web.testclient

import play.api._
import play.api.mvc._

object Client extends Controller{

  def login = Action {
    Ok("Client login/register")
  }

  def register = TODO
  
}

