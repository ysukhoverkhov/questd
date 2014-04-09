package controllers.domain.app.quest

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.app.user._
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

case class CalculateProposalThresholdsRequest(proposalsVoted: Double, proposalsLiked: Double)
case class CalculateProposalThresholdsResult()

private[domain] trait QuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates quest status taking votes into account.
   */
  def updateQuestStatus(request: UpdateQuestStatusRequest): ApiResult[UpdateQuestStatusResult] = handleDbException {
    import request._

    def capPoints(quest: Quest): Quest = {
      if (quest.rating.votersCount > Int.MaxValue / 2) {
        Logger.error("quest.rating.votersCount > Int.MaxValue / 2. this is the time to invent what to do with this.")
      }

      quest
    }

    def checkAddToRotation(quest: Quest): Quest = {
      if (quest.shouldAddToRotation) {
        db.quest.updateStatus(quest.id, QuestStatus.InRotation.toString)
        db.quest.updateLevel(quest.id, quest.calculateQuestLevel)
      }

      quest
    }

    def checkRemoveFromRotation(quest: Quest): Quest = {
      if (quest.shouldRemoveFromRotation)
        db.quest.updateStatus(quest.id, QuestStatus.RatingBanned.toString)

      quest
    }

    def checkBanQuest(quest: Quest): Quest = {
      if (quest.shouldBanQuest)
        db.quest.updateStatus(quest.id, QuestStatus.IACBanned.toString)

      quest
    }

    def checkCheatingQuest(quest: Quest): Quest = {
      if (quest.shouldCheatingQuest)
        db.quest.updateStatus(quest.id, QuestStatus.CheatingBanned.toString)

      quest

    }

    def checkRemoveQuestFromVotingByTime(quest: Quest): Quest = {
      if (quest.shouldRemoveQuestFromVotingByTime)
        db.quest.updateStatus(quest.id, QuestStatus.OldBanned.toString)

      quest
    }

    val updatedQuest =
      checkRemoveQuestFromVotingByTime(
        checkCheatingQuest(
          checkBanQuest(
            checkRemoveFromRotation(
              checkAddToRotation(
                capPoints(quest))))))

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

    val nq = db.quest.updatePoints(quest.id, -1, 1)
    updateQuestStatus(UpdateQuestStatusRequest(nq.get))

    OkApiResult(Some(SkipQuestResult()))
  }

  /**
   * Update quest params on taking quest.
   */
  def takeQuestUpdate(request: TakeQuestUpdateRequest): ApiResult[TakeQuestUpdateResult] = handleDbException {
    import request._

    val nq = db.quest.updatePoints(quest.id, ratio, 1)
    updateQuestStatus(UpdateQuestStatusRequest(nq.get))

    OkApiResult(Some(TakeQuestUpdateResult()))
  }

  /**
   * Updates quest according to vote.
   */
  def voteQuest(request: VoteQuestUpdateRequest): ApiResult[VoteQuestUpdateResult] = handleDbException {
    import request._
    import QuestProposalVote._

    def checkInc[T](v: T, c: T, n: Int = 0) = if (v == c) n + 1 else n

    val q = db.quest.updatePoints(
      quest.id,
      checkInc(vote, Cool),
      1,
      checkInc(vote, Cheating),

      checkInc(vote, IASpam),
      checkInc(vote, IAPorn))

    val q2 = if (vote == QuestProposalVote.Cool) {

      db.quest.updatePoints(
        quest.id,
        0,
        0,
        0,

        0,
        0,

        checkInc(difficulty.get, QuestDifficulty.Easy),
        checkInc(difficulty.get, QuestDifficulty.Normal),
        checkInc(difficulty.get, QuestDifficulty.Hard),
        checkInc(difficulty.get, QuestDifficulty.Extreme),

        checkInc(duration.get, QuestDuration.Minutes),
        checkInc(duration.get, QuestDuration.Hours),
        checkInc(duration.get, QuestDuration.Day),
        checkInc(duration.get, QuestDuration.TwoDays),
        checkInc(duration.get, QuestDuration.Week))
    } else {
      q
    }

    updateQuestStatus(UpdateQuestStatusRequest(q2.get))

    OkApiResult(Some(VoteQuestUpdateResult()))
  }

  
  def CalculateProposalThresholds(request: CalculateProposalThresholdsRequest): ApiResult[CalculateProposalThresholdsResult] = handleDbException {
    
    val count = db.quest.countWithStatus(QuestStatus.OnVoting.toString)

    updateConfig(api.ConfigParams.TotalDailyProposalVotes -> request.proposalsVoted.toString)
    updateConfig(api.ConfigParams.TotalDailyProposalLikes -> request.proposalsLiked.toString)
    updateConfig(api.ConfigParams.ProposalsCountOnVoting -> count.toString)
    
    OkApiResult(Some(CalculateProposalThresholdsResult()))
  }
}

