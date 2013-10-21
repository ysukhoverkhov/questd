package controllers.web.rest

import play.api._
import play.api.mvc._
import play.api.libs.json._
import controllers.domain._
import controllers.domain.jsonhelpers.AuthAPI._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain.OkApiResult
import controllers.web.rest.security._

object ProfileWS extends Controller with SecurityWS {

  def getName = Authenticated.async { implicit request =>
    Future {
      Ok(request.user.username) 
    }
  }

}

