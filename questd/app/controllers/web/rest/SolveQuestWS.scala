package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object SolveQuestWS extends Controller with AccessToWSInstance {
  
  def getQuestCost = ws.getQuestCost
  def purchaseQuest = ws.purchaseQuest
  def takeQuest = ws.takeQuest

  def proposeSolution = ws.proposeSolution
  def getSolutionProposeCost = ws.getSolutionProposeCost

  def giveUpQuest = ws.giveUpQuest
  def getQuestGiveUpCost = ws.getQuestGiveUpCost

}

