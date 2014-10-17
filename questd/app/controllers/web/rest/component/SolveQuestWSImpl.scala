package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import models.domain._

trait SolveQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

//  def getQuestCost = wrapApiCallReturnBody[WSGetQuestCostResult] { r =>
//    api.getQuestCost(GetQuestCostRequest(r.user))
//  }
//
//  def purchaseQuest = wrapApiCallReturnBody[WSPurchaseQuestResult] { r =>
//    api.purchaseQuest(PurchaseQuestRequest(r.user))
//  }

  // TODO: rename me to getQuestSolveCost
  def getTakeQuestCost = wrapApiCallReturnBody[WSGetTakeQuestCostResult] { r =>
    api.getTakeQuestCost(GetTakeQuestCostRequest(r.user))
  }

//  def takeQuest = wrapApiCallReturnBody[WSTakeQuestResult] { r =>
//    api.takeQuest(TakeQuestRequest(r.user))
//  }

  def proposeSolution = wrapJsonApiCallReturnBody[WSProposeSolutionResult] { (js, r) =>
    val v = Json.read[WSProposeSolutionRequest](js.toString)

    api.proposeSolution(ProposeSolutionRequest(r.user, v.questId, v.solutionContent))
  }

//  def getQuestGiveUpCost = wrapApiCallReturnBody[WSGetQuestGiveUpCostResult] { r =>
//    api.getQuestGiveUpCost(GetQuestGiveUpCostRequest(r.user))
//  }

//  def giveUpQuest = wrapApiCallReturnBody[WSGiveUpQuestResult] { r =>
//    api.giveUpQuest(GiveUpQuestRequest(r.user))
//  }

//  def getQuestSolutionHelpCost = wrapApiCallReturnBody[WSGetQuestSolutionHelpCostResult] { r =>
//    api.getQuestSolutionHelpCost(GetQuestSolutionHelpCostRequest(r.user))
//  }
}

