package controllers.domain.admin

import components._
import controllers.domain._
import controllers.domain.helpers._
import logic.QuestLogic
import models.domain.quest.{QuestStatus, Quest}
import models.domain.solution.SolutionStatus
import models.domain.user.User
import play.Logger

case class CleanUpObjectsRequest()
case class CleanUpObjectsResult()

private[domain] trait MaintenanceAdminAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get config section by its name.
   */
  def cleanUpObjects(request: CleanUpObjectsRequest): ApiResult[CleanUpObjectsResult] = handleDbException {

    val objectsToRemove = scala.collection.mutable.ListBuffer.empty[String]

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

    def checkBanQuest(quest: Quest): Quest = {
      if (quest.info.content.media.storage == "fb" && quest.status == QuestStatus.InRotation) {
        Logger.error(s"!!!! Banning quest $quest") // TODO: remove me.
        quest.copy(status = QuestStatus.OldBanned) // TODO: replace it with Adminbanned in 0.40.08
      } else {
        quest
      }
    }

    def rememberObjectToRemoveFromTimeline(objId: String): Unit = {
      objectsToRemove.append(objId)
    }

    def removeObjectsFromTimeline(user: User, objIds: Seq[String]): User = {
      user.copy(timeLine = user.timeLine.filterNot(p => objIds.contains(p.objectId)))
    }

    db.quest.all.foreach { quest =>
      val updatedQuest = updateQuestValues(checkBanQuest(quest))

      db.quest.update(updatedQuest)

      if (updatedQuest.status == QuestStatus.OldBanned) { // TODO: replace with AdminBanned in "0.40.08"
        rememberObjectToRemoveFromTimeline(updatedQuest.id)
      }
    }

    db.solution.all.foreach { solution =>
      db.solution.update(solution)

      if (solution.status == SolutionStatus.OldBanned) { // TODO: replace with AdminBanned in "0.40,08"
        rememberObjectToRemoveFromTimeline(solution.id)
      }
    }

    db.battle.all.foreach { battle =>
      db.battle.update(battle)
    }

    db.user.all.foreach { user =>
      db.user.update(
        removeObjectsFromTimeline(user.initialized, objectsToRemove)
      )
    }

    OkApiResult(CleanUpObjectsResult())
  }

}


