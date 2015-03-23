package controllers.web.rest.component

import controllers.domain._
import controllers.domain.admin.AllQuestsRequest
import controllers.domain.app.quest.VoteQuestRequest
import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import models.domain.ContentVote

private object DebugWSImplTypes {

  type WSShiftDailyResultResult = ShiftDailyResultResult

  case class WSTestResult(r: String)

  case class WSVoteQuestDebugRequest (
    questId: String,
    likesCount: Int,
    cheatingCount: Int,
    pornCount: Int
    )
}

trait DebugWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.DebugWSImplTypes._

  def shiftDailyResult = wrapApiCallReturnBody[WSShiftDailyResultResult] { r =>
    api.resetDailyTasks(ResetDailyTasksRequest(r.user))
    api.populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(r.user))
    api.shiftDailyResult(ShiftDailyResultRequest(r.user))
  }

  def test = wrapApiCallReturnBody[WSTestResult] { r =>

    //      shiftStats(ShiftStatsRequest(user))
//import controllers.domain.app.quest._
//	  calculateProposalThresholds(CalculateProposalThresholdsRequest(10, 3))
//      shiftHistory(ShiftHistoryRequest(user))

    OkApiResult(WSTestResult("lalai"))
  }

  def voteQuestDebug = wrapJsonApiCallReturnBody[WSTestResult] { (js, r) =>

    val v = Json.read[WSVoteQuestDebugRequest](js)

    val quest = api.allQuests(AllQuestsRequest()).body.get.quests.filter(_.id == v.questId).next()

    (1 to v.likesCount).foreach { i =>
      api.voteQuest(VoteQuestRequest(quest, ContentVote.Cool))
    }

    for (i <- 1 to v.cheatingCount) {
      api.voteQuest(VoteQuestRequest(quest, ContentVote.Cheating))
    }

    for (i <- 1 to v.pornCount) {
      api.voteQuest(VoteQuestRequest(quest, ContentVote.IAPorn))
    }

    OkApiResult(WSTestResult("Done"))
  }

  def voteSolutionDebug = wrapApiCallReturnBody[WSTestResult] { r =>
    OkApiResult(WSTestResult("Done"))
  }
}

