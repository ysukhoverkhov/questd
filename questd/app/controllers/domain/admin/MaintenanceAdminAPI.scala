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

    def updateEmptySource(user: User): User = {
      if (user.profile.analytics.source == Map.empty) {
        user.copy(
          profile = user.profile.copy(
            analytics = user.profile.analytics.copy(
              source = Map("channel" -> "Direct")
            )))
      } else {
        user
      }
    }

    db.quest.all.foreach { quest =>
      val updatedQuest = updateQuestSolutionsCount(updateQuestValues(quest))

      db.quest.update(updatedQuest)

      if (updatedQuest.status == QuestStatus.AdminBanned) {
        rememberObjectToRemoveFromTimeline(updatedQuest.id)
      }
    }

    db.solution.all.foreach { solution =>
      checkAddSolutionToAuthor(solution)
      db.solution.update(solution)

      if (solution.status == SolutionStatus.AdminBanned) {
        rememberObjectToRemoveFromTimeline(solution.id)
      }
    }

    db.battle.all.foreach { battle =>
      db.battle.update(battle)
    }

    db.user.all.foreach { user =>
      val f = updateEmptySource _
      db.user.update(
        f(removeObjectsFromTimeline(user.initialized, objectsToRemove))
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


