package controllers.domain.admin

import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._

case class AllQuestsResult(quests: List[Quest])
case class AllQuestSolutionsResult(quests: List[QuestSolution])

private [domain] trait QuestsAdminAPI { this: DBAccessor => 


  /**
   * List all Quests with approved status.
   */
  def allQuestsInRotation: ApiResult[AllQuestsResult] = handleDbException {
    Logger.info("Admin request for all quests. THIS SHOULD NOT BE CALLED IN PRODUCTION SINCE IT'S VERY SLOW!!!!!!")

    OkApiResult(Some(AllQuestsResult(List() ++ db.quest.allWithStatus(QuestStatus.InRotation.toString))))
  }

  /**
   * List all Quests with OnVoting status.
   */
  def allQuestsOnVoting: ApiResult[AllQuestsResult] = handleDbException {
    Logger.info("Admin request for all quests. THIS SHOULD NOT BE CALLED IN PRODUCTION SINCE IT'S VERY SLOW!!!!!!")

    OkApiResult(Some(AllQuestsResult(List() ++ db.quest.allWithStatus(QuestStatus.OnVoting.toString))))
  }

  /**
   * List all Quests solution s with OnVoting status.
   */
  def allQuestSolutionsOnVoting: ApiResult[AllQuestSolutionsResult] = handleDbException {
    Logger.info("Admin request for all quests. THIS SHOULD NOT BE CALLED IN PRODUCTION SINCE IT'S VERY SLOW!!!!!!")

    OkApiResult(Some(AllQuestSolutionsResult(List() ++ db.solution.allWithStatus(QuestSolutionStatus.OnVoting.toString))))
  }
}


