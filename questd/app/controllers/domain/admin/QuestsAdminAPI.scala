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
case class AllQuestSolutionsResult(quests: List[QuestSolution])

private [domain] trait QuestsAdminAPI { this: DBAccessor => 


  /**
   * List all Quests with approved status.
   */
  def allQuestsInRotation(request: AllQuestsRequest): ApiResult[AllQuestsResult] = handleDbException {
    OkApiResult(Some(AllQuestsResult(db.quest.allWithStatusAndLevels(QuestStatus.InRotation.toString, request.fromLevel, request.toLevel))))
  }

  /**
   * List all Quests with OnVoting status.
   */
  def allQuestsOnVoting: ApiResult[AllQuestsResult] = handleDbException {
    OkApiResult(Some(AllQuestsResult(db.quest.allWithStatusAndLevels(QuestStatus.OnVoting.toString, 0, 21))))
  }

  /**
   * List all Quests solution s with OnVoting status.
   */
  def allQuestSolutionsOnVoting(request: AllQuestSolutionsRequest): ApiResult[AllQuestSolutionsResult] = handleDbException {
    Logger.info("Admin request for all quests. THIS SHOULD NOT BE CALLED IN PRODUCTION SINCE IT'S VERY SLOW!!!!!!")

    OkApiResult(Some(AllQuestSolutionsResult(List() ++ db.solution.allWithStatusAndLevels(QuestSolutionStatus.OnVoting.toString, request.minLevel, request.maxLevel))))
  }
}


