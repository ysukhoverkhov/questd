package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._
import models.domain._
import models.domain.common.ContentVote

private object VoteQuestWSImplTypes {

  case class WSVoteQuestRequest(
    questId: String,
    vote: String)

  type WSVoteQuestResult = VoteQuestByUserResult
}

trait VoteQuestWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.VoteQuestWSImplTypes._

  def voteQuest = wrapJsonApiCallReturnBody[WSVoteQuestResult] { (js, r) =>

    val v = Json.read[WSVoteQuestRequest](js)
    val vote = ContentVote.withName(v.vote)

    api.voteQuestByUser(VoteQuestByUserRequest(r.user, v.questId, vote))
  }

}

