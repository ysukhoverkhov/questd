package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import controllers.web.rest.component.VoteSolutionWSImplTypes.WSHideOwnSolutionRequest
import models.domain.common.ContentVote

private object VoteQuestWSImplTypes {

  case class WSVoteQuestRequest(
    questId: String,
    vote: String)
  type WSVoteQuestResult = VoteQuestByUserResult

  case class WSHideOwnQuestRequest(
    questId: String)
  type WSHideOwnQuestsResult = HideOwnQuestResult
}

trait VoteQuestWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.VoteQuestWSImplTypes._

  def voteQuest = wrapJsonApiCallReturnBody[WSVoteQuestResult] { (js, r) =>

    val v = Json.read[WSVoteQuestRequest](js)
    val vote = ContentVote.withName(v.vote)

    api.voteQuestByUser(VoteQuestByUserRequest(r.user, v.questId, vote))
  }

  // TODO: implement me.
  def hideOwnQuest = wrapJsonApiCallReturnBody[WSHideOwnQuestsResult] { (js, r) =>
    val v = Json.read[WSHideOwnQuestRequest](js)
    api.hideOwnQuest(HideOwnQuestRequest(r.user, v.questId))
  }

}

