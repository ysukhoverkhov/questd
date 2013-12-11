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
   * calculate level of a quest with current votes.
   */
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
        (2, quest.rating.difficultyRating.extreme))

      val dur = List(
        (-2, quest.rating.durationRating.mins),
        (-1, quest.rating.durationRating.hour),
        (0, quest.rating.durationRating.day),
        (1, quest.rating.durationRating.days),
        (2, quest.rating.durationRating.week))

      maxTuple(dif)._1 + maxTuple(dur)._1
    }

    api.getUser(UserRequest(userID = Some(quest.userID))) match {
      case OkApiResult(Some(UserResult(u))) => {
        math.min(21, math.max(0, u.profile.level + levelShift))

      }
      case _ => {
        throw new Exception
      }
    }

  }

  /**
   * Are we able to add quest to rotation.
   */
  def shouldAddToRotation = {
    if ((quest.rating.points > pointsToAddQuestToRotation) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true
    else
      false
  }

  /**
   * Should we remove quest from rotation.
   */
  def shouldRemoveFromRotation = {
    if ((quest.rating.points < pointsToAddQuestToRotation / 2) && (QuestStatus.withName(quest.status) == QuestStatus.InRotation))
      true
    else
      false
  }

  /**
   * Should we ban quest.
   */
  def shouldBanQuest = {
    if ((quest.rating.iacpoints.porn.toFloat / quest.rating.votersCount > iacBanRatio)
      || (quest.rating.iacpoints.spam.toFloat / quest.rating.votersCount > iacBanRatio))
      true
    else
      false
  }

  def shouldCheatingQuest = {
    if ((quest.rating.cheating > pointsToAddQuestToRotation) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true
    else
      false
  }

  def shouldRemoveQuestFromVotingByTime = {
    if ((quest.rating.votersCount > votersToRemoveQuestFromVoting) && (QuestStatus.withName(quest.status) == QuestStatus.OnVoting))
      true
    else
      false
  }


  private def votersToRemoveQuestFromVoting = 100
  private def pointsToAddQuestToRotation = 10
  private def iacBanRatio = 0.1
}

