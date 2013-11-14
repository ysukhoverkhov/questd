package controllers.web.rest.component

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import controllers.domain.user._
import controllers.domain._
import controllers.web.rest.component.helpers._
import controllers.web.rest.component._
import controllers.web.rest.protocol._
import models.domain._

trait ProposeQuestWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  def wrapApiCall[T <: AnyRef](apiCall: User => ApiResult[T])(body: Option[T] => SimpleResult) = Authenticated.async { implicit request =>
    Future {
      apiCall(request.user) match {
        case OkApiResult(r) => {
          body(r)
        }

        case NotAuthorisedApiResult(_) => Unauthorized(
          Json.write(WSUnauthorisedResult(UnauthorisedReason.InvalidFBToken))).as(JSON)

        case _ => ServerError
      }
    }
  }

  def writeBodyInResponse[T <: AnyRef](body: Option[T]): SimpleResult = {
    body match {
      case Some(r) => Ok(Json.write[T](r)).as(JSON)
      case _ => ServerError
    }
  }

  def wrapApiCallReturnBody[T <: AnyRef](apiCall: User => ApiResult[T]) = wrapApiCall(apiCall)(writeBodyInResponse)

  
  
  def getQuestThemeCost = wrapApiCallReturnBody[WSGetQuestThemeCostResult] { user =>
    api.getQuestThemeCost(GetQuestThemeCostRequest(user))
  }

  def purchaseQuestTheme = wrapApiCallReturnBody[WSPurchaseQuestThemeResult] { user =>
    api.purchaseQuestTheme(PurchaseQuestThemeRequest(user))
  }

  def takeQuestTheme = wrapApiCallReturnBody[WSTakeQuestThemeResult] { user =>
    api.takeQuestTheme(TakeQuestThemeRequest(user))
  }
}

