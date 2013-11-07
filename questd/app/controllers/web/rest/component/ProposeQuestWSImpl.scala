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

trait ProposeQuestWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def purchaseTheme = Authenticated.async { implicit request =>
    Future {
      Ok("Purchase quest") 
    }
  }

}

