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
import org.json4s._

trait ProposeQuestWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  //////////
  // TODO: move me for reuse.
  def wrapApiCall[T <: AnyRef](apiCall: AuthenticatedRequest[AnyContent] => ApiResult[T])(body: Option[T] => SimpleResult) = Authenticated.async { implicit request =>
    Future {

      try {
        apiCall(request) match {
          case OkApiResult(r) => {
            body(r)
          }

          case NotAuthorisedApiResult(_) => Unauthorized(
            Json.write(WSUnauthorisedResult(UnauthorisedReason.InvalidFBToken))).as(JSON)

          case _ => ServerError
        }
      } catch {
        case ex @ (_: MappingException | _: org.json4s.ParserUtil$ParseException) => {
          BadRequest(ex.getMessage())
        }
        case ex: Throwable => {
          Logger.error("Api calling exception", ex)
          ServerError
        }

      }
    }
  }

  def writeBodyInResponse[T <: AnyRef](body: Option[T]): SimpleResult = {
    body match {
      case Some(r) => Ok(Json.write[T](r)).as(JSON)
      case _ => ServerError
    }
  }

  def wrapApiCallReturnBody[T <: AnyRef](apiCall: AuthenticatedRequest[AnyContent] => ApiResult[T]) = wrapApiCall(apiCall)(writeBodyInResponse)
  //////////////

  def getQuestThemeCost = wrapApiCallReturnBody[WSGetQuestThemeCostResult] { r =>
    api.getQuestThemeCost(GetQuestThemeCostRequest(r.user))
  }

  def purchaseQuestTheme = wrapApiCallReturnBody[WSPurchaseQuestThemeResult] { r =>
    api.purchaseQuestTheme(PurchaseQuestThemeRequest(r.user))
  }

  def takeQuestTheme = wrapApiCallReturnBody[WSTakeQuestThemeResult] { r =>
    api.takeQuestTheme(TakeQuestThemeRequest(r.user))
  }

  def proposeQuest = wrapApiCallReturnBody[WSProposeQuestResult] { r =>
    r.body.asText.fold {
      throw new org.json4s.ParserUtil$ParseException("Empty request", null)
    } { x =>
      val v = Json.read[QuestInfo](x)
      api.proposeQuest(ProposeQuestRequest(r.user, v))
    }
  }
}

