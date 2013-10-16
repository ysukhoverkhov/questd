package controllers.web.rest

import play.api._
import play.api.mvc._


object Application extends Controller {

  def index = Action {
//    Redirect("http://google.com/", 302)
    Ok("This is our super index. it should redirect somewhere.")
  }

}
