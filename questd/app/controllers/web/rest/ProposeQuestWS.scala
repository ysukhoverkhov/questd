package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object ProposeQuestWS extends Controller with AccessToWSInstance {
  
  def getQuestThemeCost = ws.getQuestThemeCost
  def purchaseQuestTheme = ws.purchaseQuestTheme
  
  def getQuestThemeTakeCost = ws.getQuestThemeTakeCost
  def takeQuestTheme = ws.takeQuestTheme

  def proposeQuest = ws.proposeQuest

  def giveUpQuestProposal = ws.giveUpQuestProposal
  def getQuestProposalGiveUpCost = ws.getQuestProposalGiveUpCost

}
