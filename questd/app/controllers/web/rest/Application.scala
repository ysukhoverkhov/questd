package controllers.web.rest

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    NotFound("Not found")
    //Redirect("http://google.com/")
    
    //    Ok("This is our super index. it should redirect somewhere.")
  }

}
