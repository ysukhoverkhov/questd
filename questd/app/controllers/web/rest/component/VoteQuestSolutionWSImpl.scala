package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import models.domain._

private object VoteQuestSolutionWSImplTypes {

  case class WSVoteQuestSolutionRequest(

    /**
     * id of solution we vote for.
     */
    solutionId: String,

    /**
     * @see controllers.domain.user.QuestSolutionVote
     */
    vote: String)

  type WSVoteSolutionResult = VoteSolutionByUserResult
}

trait VoteQuestSolutionWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.VoteQuestSolutionWSImplTypes._

  def voteSolution = wrapJsonApiCallReturnBody[WSVoteSolutionResult] { (js, r) =>

    val v = Json.read[WSVoteQuestSolutionRequest](js)
    val vote = ContentVote.withName(v.vote)

    api.voteSolutionByUser(VoteSolutionByUserRequest(r.user, v.solutionId, vote))

  }

}

