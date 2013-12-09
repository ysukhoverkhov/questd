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

trait VoteQuestSolutionWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def getQuestSolutionToVote = wrapApiCallReturnBody[WSGetQuestSolutionToVoteResult] { r =>
    api.getQuestSolutionToVote(GetQuestSolutionToVoteRequest(r.user))
  }

  // TODO implement me.
  def voteQuestSolution = wrapJsonApiCallReturnBody[WSVoteQuestSolutionResult] { (js, r) =>

    val v = Json.read[WSQuestProposalVoteRequest](js)
    val vote = QuestProposalVote.withName(v.vote)

    val duration = v.duration match {
      case None => None
      case Some(d) => Some(QuestDuration.withName(d))
    }
    val difficulty = v.difficulty match {
      case None => None
      case Some(d) => Some(QuestDifficulty.withName(d))
    }

    api.voteQuestSolution(VoteQuestSolutionRequest(r.user/*, vote, duration, difficulty*/))

  }

}

