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

trait SolveQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def getQuestCost = wrapApiCallReturnBody[WSGetQuestCostResult] { r =>
    api.getQuestCost(GetQuestCostRequest(r.user))
  }
  
  def purchaseQuest = wrapApiCallReturnBody[WSPurchaseQuestResult] { r =>
    api.purchaseQuest(PurchaseQuestRequest(r.user))
  }
  
  def takeQuest = wrapApiCallReturnBody[WSTakeQuestResult] { r =>
    api.takeQuest(TakeQuestRequest(r.user))
  }

  def proposeSolution = TODO
  def getSolutionProposeCost = TODO

  def giveUpQuest = TODO
  def getQuestGiveUpCost = TODO
}

// TODO skip already purchased quests and themes in crawler to start new sequence on next day.