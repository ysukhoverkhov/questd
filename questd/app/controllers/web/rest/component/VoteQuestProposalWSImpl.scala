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

trait VoteQuestProposalWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  def getQuestToVote = wrapApiCallReturnBody[WSGetQuestToVoteResult] { r =>
    api.getQuestToVote(GetQuestToVoteRequest(r.user))
  }

  // TODO make additional wrapper for working with json bodies.
  // TODO replace all occurences. find them with "Empty request"
  def voteQuestProposal = wrapApiCallReturnBody[WSVoteQuestResult] { r =>

    r.body.asJson.fold {
      throw new org.json4s.ParserUtil$ParseException("Empty request", null)
    } { x =>
      val v = Json.read[WSQuestProposalVoteRequest](x.toString)
      val vote = QuestProposalVote.withName(v.vote)
      
      val duration = v.duration match {
        case None => None
        case Some(d) => Some(QuestDuration.withName(d))
      }
      val difficulty = v.difficulty match {
        case None => None
        case Some(d) => Some(QuestDifficulty.withName(d))
      }

      api.voteQuestProposal(VoteQuestRequest(r.user, vote, duration, difficulty))
    }

  }

}

