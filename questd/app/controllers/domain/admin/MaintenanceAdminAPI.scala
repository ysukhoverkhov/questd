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

case class ExportAnalyticsRequest()
case class ExportAnalyticsResult(data: String)

private[domain] trait MaintenanceAdminAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Cleanup all database objects.
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

    def checkAddSolutionToAuthor(solution: Solution): Unit = {
      db.user.readById(solution.info.authorId).fold(){ author =>
        if (!author.stats.solvedQuests.contains(solution.info.questId)) {
          db.user.recordQuestSolving(
            id = author.id,
            questId = solution.info.questId,
            solutionId = solution.id,
            removeBookmark = false)
        }
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

      checkAddSolutionToAuthor(updatedSolution)
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
    }

    OkApiResult(CleanUpObjectsResult())
  }

  /**
   *
   */
  def exportAnalytics(request: ExportAnalyticsRequest): ApiResult[ExportAnalyticsResult] = handleDbException {
    val data =

      (List(
        List[String](
          "id",
          "profile.analytics.profileCreationDate",
          "profile.analytics.source.channel",
          "profile.analytics.source.campaign",
          "profile.analytics.source.tags",
          "auth.lastLogin",
          "demo.cultureId",
          "profile.publicProfile.level",
          "profile.assets.coins",
          "profile.assets.rating",
          "stats.createdQuests.length",
          "stats.solvedQuests.size",
          "stats.participatedBattles.size",
          "following.length",
          "followers.length",
          "banned.length"
        ).mkString(",")
      ) ::: db.user.all.map[String] { u =>
        List[String](
          u.id,
          u.profile.analytics.profileCreationDate.toString,
          u.profile.analytics.source.getOrElse("channel", ""),
          u.profile.analytics.source.getOrElse("campaign", ""),
          u.profile.analytics.source.getOrElse("tags", ""),
          u.auth.lastLogin.map[String](_.toString).getOrElse(""),
          u.demo.cultureId.getOrElse(""),
          u.profile.publicProfile.level.toString,
          u.profile.assets.coins.toString,
          u.profile.assets.rating.toString,
          u.stats.createdQuests.length.toString,
          u.stats.solvedQuests.size.toString,
          u.stats.participatedBattles.size.toString,
          u.following.length.toString,
          u.followers.length.toString,
          u.banned.length.toString
        ).map {
          case "" => """"""""
          case v => v
        }.mkString(",")
      }.toList).mkString("\n")

    OkApiResult(ExportAnalyticsResult(data))
  }

}


