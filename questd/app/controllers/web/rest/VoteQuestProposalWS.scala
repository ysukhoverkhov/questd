package controllers.web.rest

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object VoteQuestProposalWS extends Controller with AccessToWSInstance {
  
  def getQuestProposalToVote = ws.getQuestProposalToVote
  def voteQuestProposal = ws.voteQuestProposal

}

