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



trait VoteQuestWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def getQuestToVote = wrapApiCallReturnBody[WSGetQuestToVoteResult] { r =>
    api.getQuestToVote(GetQuestToVoteRequest(r.user))
  }
  
  def voteQuest = wrapApiCallReturnBody[WSVoteQuestResult] { r =>
    api.voteQuest(VoteQuestRequest(r.user))
  }
  

}

