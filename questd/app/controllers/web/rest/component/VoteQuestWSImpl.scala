package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import models.domain._

trait VoteQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def voteQuest = wrapJsonApiCallReturnBody[WSVoteQuestResult] { (js, r) =>

    val v = Json.read[WSVoteQuestRequest](js)
    val vote = ContentVote.withName(v.vote)

    api.voteQuestByUser(VoteQuestByUserRequest(r.user, v.questId, vote))
  }

}

