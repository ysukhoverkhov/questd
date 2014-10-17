package controllers.web.rest

import play.api.mvc._
import controllers.web.rest.component.helpers.AccessToWSInstance

object VoteQuestProposalWS extends Controller with AccessToWSInstance {

  // TODO: clean me up.
//  def getQuestProposalToVote = ws.getQuestProposalToVote
  def voteQuestProposal = ws.voteQuestProposal

}

