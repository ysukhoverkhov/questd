package controllers.web.rest

import play.api.mvc._

object Application extends Controller {

  def index = Action {
    NotFound("Not found")
  }

}
