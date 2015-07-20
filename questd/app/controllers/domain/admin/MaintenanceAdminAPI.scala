package controllers.domain.admin

import components._
import controllers.domain._
import controllers.domain.helpers._
import logic.QuestLogic
import models.domain.quest.{Quest, QuestStatus}
import models.domain.solution.{Solution, SolutionStatus}
import models.domain.user.User

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

    def updateQuestSolutionsCount(quest: Quest): Quest = {
      quest.copy(
        solutionsCount = db.solution.allWithParams(questIds = List(quest.id)).size
      )
    }


    def checkBanQuest(quest: Quest): Quest = {
      if (quest.info.content.media.storage == "fb" && quest.status == QuestStatus.InRotation) {
        quest.copy(status = QuestStatus.AdminBanned)
      } else {
        quest
      }
    }

    def checkBanSolution(solution: Solution): Solution = {
      if (solution.info.content.media.storage == "fb" && solution.status == SolutionStatus.InRotation) {
        solution.copy(status = SolutionStatus.AdminBanned)
      } else {
        solution
      }
    }

    def rememberObjectToRemoveFromTimeline(objId: String): Unit = {
      objectsToRemove.append(objId)
    }

    def removeObjectsFromTimeline(user: User, objIds: Seq[String]): User = {
      user.copy(timeLine = user.timeLine.filterNot(p => objIds.contains(p.objectId)))
    }

    db.quest.all.foreach { quest =>
      val updatedQuest = updateQuestSolutionsCount(updateQuestValues(checkBanQuest(quest)))

      db.quest.update(updatedQuest)

      if (updatedQuest.status == QuestStatus.AdminBanned) {
        rememberObjectToRemoveFromTimeline(updatedQuest.id)
      }
    }

    db.solution.all.foreach { solution =>
      val updatedSolution = checkBanSolution(solution)

      db.solution.update(updatedSolution)

      if (updatedSolution.status == SolutionStatus.AdminBanned) {
        rememberObjectToRemoveFromTimeline(updatedSolution.id)
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

    db.tutorial.all.foreach { tutorial =>
      db.tutorial.update(tutorial)
//      tutorial.elements.foreach{ element =>
//        db.tutorial.updateElement(tutorial.id, element.copy(actions = List(element.action, TutorialAction(TutorialActionType.CloseTutorialElement, params = Map("elementId" -> element.id)))))
//      }
    }

    OkApiResult(CleanUpObjectsResult())
  }

}


