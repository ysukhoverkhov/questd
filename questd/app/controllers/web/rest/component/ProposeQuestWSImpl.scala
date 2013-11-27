package controllers.web.rest.component

import play.api._
import play.api.mvc._
import controllers.domain.user._
import controllers.domain._
import controllers.web.rest.component.helpers._
import controllers.web.rest.component._
import controllers.web.rest.protocol._
import models.domain._
import org.json4s._

trait ProposeQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def getQuestThemeCost = wrapApiCallReturnBody[WSGetQuestThemeCostResult] { r =>
    api.getQuestThemeCost(GetQuestThemeCostRequest(r.user))
  }

  def purchaseQuestTheme = wrapApiCallReturnBody[WSPurchaseQuestThemeResult] { r =>
    api.purchaseQuestTheme(PurchaseQuestThemeRequest(r.user))
  }

  def getQuestThemeTakeCost = wrapApiCallReturnBody[WSGetQuestThemeTakeCostResult] { r =>
    api.getQuestThemeTakeCost(GetQuestThemeTakeCostRequest(r.user))
  }

  def takeQuestTheme = wrapApiCallReturnBody[WSTakeQuestThemeResult] { r =>
    api.takeQuestTheme(TakeQuestThemeRequest(r.user))
  }

  
  def proposeQuest = wrapApiCallReturnBody[WSProposeQuestResult] { r =>
    r.body.asJson.fold {
      throw new org.json4s.ParserUtil$ParseException("Empty request", null)
    } { x =>
      val v = Json.read[QuestInfo](x.toString)
      api.proposeQuest(ProposeQuestRequest(r.user, v))
    }
  }
  
  
  def giveUpQuestProposal = wrapApiCallReturnBody[WSGiveUpQuestProposalResult] { r =>
    api.giveUpQuestProposal(GiveUpQuestProposalRequest(r.user))
  }
  
  def getQuestProposalGiveUpCost = wrapApiCallReturnBody[WSGetQuestProposalGiveUpCostResult] { r =>
    api.getQuestProposalGiveUpCost(GetQuestProposalGiveUpCostRequest(r.user))
  }
}

