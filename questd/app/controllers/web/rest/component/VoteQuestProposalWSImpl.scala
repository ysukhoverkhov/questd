package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import models.domain._

trait VoteQuestProposalWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  // TODO: clean me up.
//  def getQuestProposalToVote = wrapApiCallReturnBody[WSGetQuestProposalToVoteResult] { r =>
//    api.getQuestProposalToVote(GetQuestProposalToVoteRequest(r.user))
//  }

  def voteQuestProposal = wrapJsonApiCallReturnBody[WSVoteQuestProposalResult] { (js, r) =>

    val v = Json.read[WSQuestProposalVoteRequest](js)
    val vote = QuestProposalVote.withName(v.vote)

    val duration = QuestDuration.withName(v.duration)
    val difficulty = QuestDifficulty.withName(v.difficulty)

    api.voteQuestProposal(VoteQuestProposalRequest(r.user, vote, duration, difficulty))
  }

}

