package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object SolveQuestWS extends Controller with AccessToWSInstance {

  def getQuestCost = ws.getQuestCost
  def purchaseQuest = ws.purchaseQuest

  def getTakeQuestCost = ws.getTakeQuestCost
  def takeQuest = ws.takeQuest

  def proposeSolution = ws.proposeSolution

  def getQuestGiveUpCost = ws.getQuestGiveUpCost
  def giveUpQuest = ws.giveUpQuest

  def getQuestSolutionHelpCost = ws.getQuestSolutionHelpCost
}

