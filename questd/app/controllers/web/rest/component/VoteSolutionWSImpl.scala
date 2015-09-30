package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import models.domain.common.ContentVote

private object VoteSolutionWSImplTypes {

  case class WSVoteSolutionRequest(

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

trait VoteSolutionWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.VoteSolutionWSImplTypes._

  def voteSolution = wrapJsonApiCallReturnBody[WSVoteSolutionResult] { (js, r) =>

    val v = Json.read[WSVoteSolutionRequest](js)
    val vote = ContentVote.withName(v.vote)

    api.voteSolutionByUser(VoteSolutionByUserRequest(r.user, v.solutionId, vote))
  }
}

