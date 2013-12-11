package logic

import components.componentregistry.ComponentRegistrySingleton
import play.Logger
import models.domain._
import controllers.domain.user.UserRequest
import controllers.domain.OkApiResult
import controllers.domain.user.UserResult

class QuestLogic(val quest: Quest) {

  lazy val api = ComponentRegistrySingleton.api

  /**
   * Check should quest change its status or should not.
   */
  
  // TODO split me and move modifications to API, but leave check here.
  def updateStatus: Quest = {
    {
      checkAddToRotation.checkRemoveFromRotation.checkBanQuest.checkCheatingQuest.checkRemoveQuestFromVotingByTime.capPoints
    }.quest
  }

  private def checkAddToRotation: QuestLogic = {

    def calculateQuestLevel = {

      def maxTuple(o: List[(Int, Int)]): (Int, Int) = {
        if (o.size == 1) {
          o.head
        } else {
          val r = maxTuple(o.tail)

          if (o.head._2 >= r._2)
            o.head
          else
            r
        }
      }
      
      def levelShift = {
        val dif = List(
            (-1, quest.rating.difficultyRating.easy),
            (0, quest.rating.difficultyRating.normal),
            (1, quest.rating.difficultyRating.hard),
            (2, quest.rating.difficultyRating.extreme)
        )
        
        val dur = List(
            (-2, quest.rating.durationRating.mins),
            (-1, quest.rating.durationRating.hour),
            (0, quest.rating.durationRating.day),
            (1, quest.rating.durationRating.days),
            (2, quest.rating.durationRating.week)
        )

        maxTuple(dif)._1 + maxTuple(dur)._1
      }

      val l = api.user(UserRequest(userID = Some(quest.userID))) match {
        case OkApiResult(Some(UserResult(u))) => {
          u.profile.level
        }
        case _ => {
          throw new Exception
        }
      }

      math.min(21, math.max(0, l + levelShift))
    }

    try {
      if ((quest.rating.points > pointsToAddQuestToRotation) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
        new QuestLogic(quest.copy(
          status = QuestStatus.InRotation.toString,
          info = quest.info.copy(
            level = calculateQuestLevel)))
      else
        this
    } catch {
      case e: Exception => this
    }
  }

  private def checkRemoveFromRotation: QuestLogic = {
    if ((quest.rating.points < pointsToAddQuestToRotation / 2) && (QuestStatus.withName(quest.status) == QuestStatus.InRotation))
      new QuestLogic(quest.copy(status = QuestStatus.RatingBanned.toString))
    else
      this
  }

  private def checkBanQuest: QuestLogic = {
    if ((quest.rating.iacpoints.porn.toFloat / quest.rating.votersCount > iacBanRatio)
      || (quest.rating.iacpoints.spam.toFloat / quest.rating.votersCount > iacBanRatio))
      new QuestLogic(quest.copy(status = QuestStatus.IACBanned.toString))
    else
      this
  }

  private def checkCheatingQuest: QuestLogic = {
    if ((quest.rating.cheating > pointsToAddQuestToRotation) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      new QuestLogic(quest.copy(status = QuestStatus.CheatingBanned.toString))
    else
      this
  }

  private def checkRemoveQuestFromVotingByTime: QuestLogic = {
    if ((quest.rating.votersCount > votersToRemoveQuestFromVoting) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      new QuestLogic(quest.copy(status = QuestStatus.OldBanned.toString))
    else
      this
  }

  private def capPoints: QuestLogic = {
    if (quest.rating.points > Int.MaxValue / 2)
      new QuestLogic(quest.copy(rating = quest.rating.copy(points = quest.rating.points / 2)))
    else
      this
  }

  private def votersToRemoveQuestFromVoting = 100
  private def pointsToAddQuestToRotation = 10
  private def iacBanRatio = 0.1
}

