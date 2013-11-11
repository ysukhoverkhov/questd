package controllers.web.rest.component

import play.api._
import play.api.mvc._
import play.api.libs.json._
import controllers.domain._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain.OkApiResult
import controllers.web.rest.component.helpers.QuestController
import controllers.web.rest.component._

trait ProfileWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def getName = Authenticated.async { implicit request =>
    Future {
      Ok("FBID - " + request.user.auth.fbid) 
    }
  }

}

