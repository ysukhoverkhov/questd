package controllers.web.rest

import play.api._
import play.api.mvc._

object LoginAPI extends Controller {

  def login = Action { request =>
    {
      request.body.asJson match {
        case Some(text) => Ok(text)
        case None => Ok("This is not a valid request")
      }
    }
  }

  def register = Action { request =>
    {
      request.body.asJson match {
        case Some(text) => Ok(text)
        case None => Ok("This is not a valid request")
      }
    }
  }

}

