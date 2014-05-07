package controllers.web.rest.component

import play.api._
import play.api.mvc._
import controllers.domain.app.user._
import controllers.domain._
import controllers.web.rest.component.helpers._
import controllers.web.rest.component._
import controllers.web.rest.protocol._
import models.domain._
import org.json4s._

trait SolveQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def getQuestCost = wrapApiCallReturnBody[WSGetQuestCostResult] { r =>
    api.getQuestCost(GetQuestCostRequest(r.user))
  }

  def purchaseQuest = wrapApiCallReturnBody[WSPurchaseQuestResult] { r =>
    api.purchaseQuest(PurchaseQuestRequest(r.user))
  }

  def getTakeQuestCost = wrapApiCallReturnBody[WSGetTakeQuestCostResult] { r =>
    api.getTakeQuestCost(GetTakeQuestCostRequest(r.user))
  }

  def takeQuest = wrapApiCallReturnBody[WSTakeQuestResult] { r =>
    api.takeQuest(TakeQuestRequest(r.user))
  }

  def proposeSolution = wrapJsonApiCallReturnBody[WSProposeSolutionResult] { (js, r) =>
    val v = Json.read[QuestSolutionInfoContent](js.toString)

    // Check for existing content type.
    ContentType.withName(v.media.contentType)

    api.proposeSolution(ProposeSolutionRequest(r.user, v))
  }

  def getQuestGiveUpCost = wrapApiCallReturnBody[WSGetQuestGiveUpCostResult] { r =>
    api.getQuestGiveUpCost(GetQuestGiveUpCostRequest(r.user))
  }

  def giveUpQuest = wrapApiCallReturnBody[WSGiveUpQuestResult] { r =>
    api.giveUpQuest(GiveUpQuestRequest(r.user))
  }
}

