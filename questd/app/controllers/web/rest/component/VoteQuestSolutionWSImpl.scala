package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import models.domain._

trait VoteQuestSolutionWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def voteSolution = wrapJsonApiCallReturnBody[WSVoteSolutionResult] { (js, r) =>

    val v = Json.read[WSVoteQuestSolutionRequest](js)
    val vote = ContentVote.withName(v.vote)

    api.voteSolution(VoteSolutionRequest(r.user, v.solutionId, vote))

  }

}

