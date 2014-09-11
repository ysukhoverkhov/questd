package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import models.domain._

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


  def proposeQuest = wrapJsonApiCallReturnBody[WSProposeQuestResult] { (js, r) =>
      val v = Json.read[QuestInfoContent](js)

      api.proposeQuest(ProposeQuestRequest(r.user, v))
  }


  def giveUpQuestProposal = wrapApiCallReturnBody[WSGiveUpQuestProposalResult] { r =>
    api.giveUpQuestProposal(GiveUpQuestProposalRequest(r.user))
  }

  def getQuestProposalGiveUpCost = wrapApiCallReturnBody[WSGetQuestProposalGiveUpCostResult] { r =>
    api.getQuestProposalGiveUpCost(GetQuestProposalGiveUpCostRequest(r.user))
  }

  def getQuestProposalHelpCost = wrapApiCallReturnBody[WSGetQuestProposalHelpCostResult] { r =>
    api.getQuestProposalHelpCost(GetQuestProposalHelpCostRequest(r.user))
  }
}

