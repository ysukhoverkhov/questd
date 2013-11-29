package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object VoteQuestProposalWS extends Controller with AccessToWSInstance {
  
  def getQuestToVote = ws.getQuestToVote
  def voteQuestProposal = ws.voteQuestProposal

}

