package controllers.web.rest

import play.api._
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    NotFound("Not found")
  }

}
