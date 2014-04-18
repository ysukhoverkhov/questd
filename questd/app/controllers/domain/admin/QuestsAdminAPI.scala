package controllers.domain.admin

import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._

case class AllQuestsRequest(fromLevel: Int, toLevel: Int)
case class AllQuestsResult(quests: Iterator[Quest])


case class AllQuestSolutionsRequest(minLevel: Int, maxLevel: Int)
case class AllQuestSolutionsResult(quests: Iterator[QuestSolution])

private [domain] trait QuestsAdminAPI { this: DBAccessor => 


  /**
   * List all Quests with approved status.
   */
  def allQuestsInRotation(request: AllQuestsRequest): ApiResult[AllQuestsResult] = handleDbException {
    OkApiResult(Some(AllQuestsResult(db.quest.allWithParams(
        status = Some(QuestStatus.InRotation.toString),
        levels = Some(request.fromLevel, request.toLevel)))))
  }

  /**
   * List all Quests with OnVoting status.
   */
  def allQuestsOnVoting: ApiResult[AllQuestsResult] = handleDbException {
    OkApiResult(Some(AllQuestsResult(db.quest.allWithParams(
        status = Some(QuestStatus.OnVoting.toString),
        levels = Some(0, logic.constants.maxLevel)))))
  }

  /**
   * List all Quests solution s with OnVoting status.
   */
  def allQuestSolutionsOnVoting(request: AllQuestSolutionsRequest): ApiResult[AllQuestSolutionsResult] = handleDbException {
    OkApiResult(Some(AllQuestSolutionsResult(db.solution.allWithStatusAndLevels(QuestSolutionStatus.OnVoting.toString, request.minLevel, request.maxLevel))))
  }
}


