package controllers.web.rest.component

import play.api._
import play.api.mvc._
import controllers.domain.app.user._
import controllers.domain._
import controllers.web.rest.component.helpers._
import controllers.web.rest.component._
import controllers.web.rest.protocol._
import models.domain._
import org.json4s._

trait VoteQuestSolutionWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

//  def getQuestSolutionToVote = wrapApiCallReturnBody[WSGetQuestSolutionToVoteResult] { r =>
//    api.getQuestSolutionToVote(GetQuestSolutionToVoteRequest(r.user))
//  }

  def voteQuestSolution = wrapJsonApiCallReturnBody[WSVoteQuestSolutionResult] { (js, r) =>

    Logger.debug("REST - Voting quest solution")

    val v = Json.read[WSQuestSolutionVoteRequest](js)
    val vote = QuestSolutionVote.withName(v.vote)

    api.voteQuestSolution(VoteQuestSolutionRequest(r.user, vote))

  }

}

