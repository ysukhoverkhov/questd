package controllers.domain.admin

import components._
import controllers.domain._
import controllers.domain.helpers._
import logic.QuestLogic
import models.domain.quest.Quest

case class CleanUpObjectsRequest()
case class CleanUpObjectsResult()

private[domain] trait MaintenanceAdminAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get config section by its name.
   */
  def cleanUpObjects(request: CleanUpObjectsRequest): ApiResult[CleanUpObjectsResult] = handleDbException {

    db.user.all.foreach { user =>
      db.user.update(user.initialized)
    }


    def updateQuestValues(quest: Quest): Quest = {
      quest.copy(
        info = quest.info.copy(
          solveCost = QuestLogic.costOfSolvingQuest(quest.info.level),
          solveReward = QuestLogic.rewardForSolvingQuest(quest.info.level, this),
          victoryReward = QuestLogic.rewardForWinningBattle(quest.info.level, this),
          defeatReward = QuestLogic.rewardForLosingBattle(quest.info.level, this)
        )
      )
    }

    db.quest.all.foreach { quest =>
      db.quest.update(
        updateQuestValues(quest)
      )
    }

    db.solution.all.foreach { solution =>
      db.solution.update(solution)
    }

    db.battle.all.foreach { battle =>
      db.battle.update(battle)
    }

    OkApiResult(CleanUpObjectsResult())
  }

}


