package controllers.web.rest.component

import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.json.Json._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain.user._
import controllers.domain._
import controllers.web.rest.component.helpers.QuestController
import controllers.web.rest.component._
import controllers.web.rest.protocol._


trait ProposeQuestWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def getQuestThemeCost = Authenticated.async { implicit request =>
    Future {
      api.getQuestThemeCost(GetQuestThemeCostRequest(request.user)) match {
        case OkApiResult(Some(r)) => {
          Ok(toJson(r))
        }

        case NotAuthorisedApiResult(_) => Unauthorized(toJson(WSUnauthorisedResult(UnauthorisedReason.InvalidFBToken)))

        case _ => ServerError
      }

    }
  }

  def getPurchasedQuestTheme = Authenticated.async { implicit request =>
    Future {
      Ok("Purchase quest")
    }
  }

  def purchaseQuestTheme = Authenticated.async { implicit request =>
    Future {
      Ok("Purchase quest")
    }
  }

}

