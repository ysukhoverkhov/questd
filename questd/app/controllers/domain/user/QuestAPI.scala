package controllers.domain.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger

case class UpdateQuestStatusRequest(quest: Quest)
case class UpdateQuestStatusResult()

case class SkipQuestRequest(quest: Quest)
case class SkipQuestResult()

case class TakeQuestUpdateRequest(quest: Quest, ratio: Int)
case class TakeQuestUpdateResult()

case class VoteQuestUpdateRequest(
  quest: Quest,
  vote: QuestProposalVote.Value,
  duration: Option[QuestDuration.Value],
  difficulty: Option[QuestDifficulty.Value])
case class VoteQuestUpdateResult()

private[domain] trait QuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates quest status taking votes into account.
   */
  def updateQuestStatus(request: UpdateQuestStatusRequest): ApiResult[UpdateQuestStatusResult] = handleDbException {
    import request._

    def capPoints(quest: Quest): Quest = {
      if (quest.rating.points > Int.MaxValue / 2)
        quest.copy(rating = quest.rating.copy(points = quest.rating.points / 2))
      else
        quest
    }

    def checkAddToRotation(quest: Quest): Quest = {
      if (quest.shouldAddToRotation)
        quest.copy(
          status = QuestStatus.InRotation.toString,
          info = quest.info.copy(
            level = quest.calculateQuestLevel))
      else
        quest
    }

    def checkRemoveFromRotation(quest: Quest): Quest = {
      if (quest.shouldRemoveFromRotation)
        quest.copy(status = QuestStatus.RatingBanned.toString)
      else
        quest
    }

    def checkBanQuest(quest: Quest): Quest = {
      if (quest.shouldBanQuest)
        quest.copy(status = QuestStatus.IACBanned.toString)
      else
        quest
    }

    def checkCheatingQuest(quest: Quest): Quest = {
      if (quest.shouldCheatingQuest)
        quest.copy(status = QuestStatus.CheatingBanned.toString)
      else
        quest

    }

    def checkRemoveQuestFromVotingByTime(quest: Quest): Quest = {
      if (quest.shouldRemoveQuestFromVotingByTime)
        quest.copy(status = QuestStatus.OldBanned.toString)
      else
        quest

    }

    val updatedQuest =
      checkRemoveQuestFromVotingByTime(
        checkCheatingQuest(
          checkBanQuest(
            checkRemoveFromRotation(
              checkAddToRotation(
                capPoints(quest))))))

    db.quest.update(updatedQuest)

    if (updatedQuest.status != quest.status) {
      val authorID = quest.authorUserID
      db.user.readByID(authorID) match {
        case None => Logger.error("Unable to find author of quest user " + authorID)
        case Some(author) => {
          rewardQuestProposalAuthor(RewardQuestProposalAuthorRequest(updatedQuest, author))
        }
      }
    }

    OkApiResult(Some(UpdateQuestStatusResult()))
  }

  /**
   * User has skipped a quest. Update quest stats accordingly.
   */
  def skipQuest(request: SkipQuestRequest): ApiResult[SkipQuestResult] = handleDbException {
    import request._

    val nq = quest.copy(
      rating = quest.rating.copy(
        points = quest.rating.points - 1,
        votersCount = quest.rating.votersCount + 1))
    db.quest.update(nq)

    updateQuestStatus(UpdateQuestStatusRequest(nq))

    OkApiResult(Some(SkipQuestResult()))
  }

  /**
   * Update quest params on taking quest.
   */
  def takeQuestUpdate(request: TakeQuestUpdateRequest): ApiResult[TakeQuestUpdateResult] = handleDbException {
    import request._

    val nq = quest.copy(
      rating = quest.rating.copy(
        points = quest.rating.points + ratio,
        votersCount = quest.rating.votersCount + 1))
    db.quest.update(nq)

    updateQuestStatus(UpdateQuestStatusRequest(nq))

    OkApiResult(Some(TakeQuestUpdateResult()))
  }

  /**
   * Updates quest according to vote.
   */
  def voteQuest(request: VoteQuestUpdateRequest): ApiResult[VoteQuestUpdateResult] = handleDbException {
    import request._
    import QuestProposalVote._

    def checkInc[T](v: T, c: T, n: Int) = if (v == c) n + 1 else n

    val q2 = quest.copy(
      rating = quest.rating.copy(
        votersCount = quest.rating.votersCount + 1,
        points = checkInc(vote, Cool, quest.rating.points),
        cheating = checkInc(vote, Cheating, quest.rating.cheating),
        iacpoints = quest.rating.iacpoints.copy(
          spam = checkInc(vote, IASpam, quest.rating.iacpoints.spam),
          porn = checkInc(vote, IAPorn, quest.rating.iacpoints.porn))))

    val q3 = if (vote == QuestProposalVote.Cool) {
      q2.copy(rating = q2.rating.copy(
        difficultyRating = q2.rating.difficultyRating.copy(
          easy = checkInc(difficulty.get, QuestDifficulty.Easy, q2.rating.difficultyRating.easy),
          normal = checkInc(difficulty.get, QuestDifficulty.Normal, q2.rating.difficultyRating.normal),
          hard = checkInc(difficulty.get, QuestDifficulty.Normal, q2.rating.difficultyRating.hard),
          extreme = checkInc(difficulty.get, QuestDifficulty.Normal, q2.rating.difficultyRating.extreme)),
        durationRating = q2.rating.durationRating.copy(
          mins = checkInc(duration.get, QuestDuration.Minutes, q2.rating.durationRating.mins),
          hour = checkInc(duration.get, QuestDuration.Hours, q2.rating.durationRating.hour),
          day = checkInc(duration.get, QuestDuration.Day, q2.rating.durationRating.day),
          days = checkInc(duration.get, QuestDuration.TwoDays, q2.rating.durationRating.days),
          week = checkInc(duration.get, QuestDuration.Week, q2.rating.durationRating.week))))
    } else {
      q2
    }

    db.quest.update(q3)

    updateQuestStatus(UpdateQuestStatusRequest(q3))

    OkApiResult(Some(VoteQuestUpdateResult()))
  }

}

