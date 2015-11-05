package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import controllers.web.rest.component.VoteQuestWSImplTypes.{WSVoteQuestRequest, WSVoteQuestResult}
import models.domain.common.ContentVote

private object VoteSolutionWSImplTypes {

  /**
    * @param solutionId id of solution we vote for.
    * @param vote Vote value.
    */
  case class WSVoteSolutionRequest(
    solutionId: String,
    vote: String)
  type WSVoteSolutionResult = VoteSolutionByUserResult

  case class WSHideOwnSolutionRequest(
    solutionId: String)
  type WSHideOwnSolutionResult = HideOwnSolutionResult
}

trait VoteSolutionWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.VoteSolutionWSImplTypes._

  def voteSolution = wrapJsonApiCallReturnBody[WSVoteSolutionResult] { (js, r) =>
    val v = Json.read[WSVoteSolutionRequest](js)
    val vote = ContentVote.withName(v.vote)
    api.voteSolutionByUser(VoteSolutionByUserRequest(r.user, v.solutionId, vote))
  }

  def hideOwnSolution = wrapJsonApiCallReturnBody[WSHideOwnSolutionResult] { (js, r) =>
    val v = Json.read[WSHideOwnSolutionRequest](js)
    api.hideOwnSolution(HideOwnSolutionRequest(r.user, v.solutionId))
  }

}

