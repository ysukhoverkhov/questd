package controllers.domain.app.quest

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.app.user._
import controllers.domain.helpers._
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
  duration: QuestDuration.Value,
  difficulty: QuestDifficulty.Value)
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
        db.quest.updateInfo(quest.id, quest.calculateQuestLevel, quest.calculateDuration.toString, quest.calculateDifficulty.toString)
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
      val authorId = quest.authorUserId
      db.user.readById(authorId) match {
        case None => Logger.error("Unable to find author of quest user " + authorId)
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

    {
      db.quest.updatePoints(quest.id, -1, 1)
    } ifSome { v =>
      updateQuestStatus(UpdateQuestStatusRequest(v))
    } ifOk {
      OkApiResult(Some(SkipQuestResult()))
    }

  }

  /**
   * Update quest params on taking quest.
   */
  def takeQuestUpdate(request: TakeQuestUpdateRequest): ApiResult[TakeQuestUpdateResult] = handleDbException {
    import request._

    {
      db.quest.updatePoints(quest.id, ratio, 1)
    } ifSome { v =>
      updateQuestStatus(UpdateQuestStatusRequest(v))
    } ifOk {
      OkApiResult(Some(TakeQuestUpdateResult()))
    }

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

        checkInc(difficulty, QuestDifficulty.Easy),
        checkInc(difficulty, QuestDifficulty.Normal),
        checkInc(difficulty, QuestDifficulty.Hard),
        checkInc(difficulty, QuestDifficulty.Extreme),

        checkInc(duration, QuestDuration.Minutes),
        checkInc(duration, QuestDuration.Hour),
        checkInc(duration, QuestDuration.Day),
        checkInc(duration, QuestDuration.Week))
    } else {
      q
    } 
    
    q ifSome { v => 
      updateQuestStatus(UpdateQuestStatusRequest(v))
    } ifOk {
      OkApiResult(Some(VoteQuestUpdateResult()))
    }
  }

  def calculateProposalThresholds(request: CalculateProposalThresholdsRequest): ApiResult[CalculateProposalThresholdsResult] = handleDbException {

    val proposalsOnVoting = db.quest.countWithStatus(QuestStatus.OnVoting.toString)
    val daysForQuestToEnter: Long = config(ConfigParams.ProposalNormalDaysToEnterRotation).toInt
    val likesToAddToRotation: Long = Math.round(request.proposalsLiked / proposalsOnVoting * daysForQuestToEnter)
    val votesToRemoveFromRotation: Long = Math.round(request.proposalsVoted / proposalsOnVoting * daysForQuestToEnter)
    val ratioToRemoveFromRotation: Double = request.proposalsLiked / request.proposalsVoted * config(ConfigParams.ProposalWorstLikesRatio).toDouble
    Logger.error(ratioToRemoveFromRotation.toString + " ")

    updateConfig(ConfigParams.ProposalLikesToEnterRotation -> likesToAddToRotation.toString)
    updateConfig(ConfigParams.ProposalVotesToLeaveVoting -> votesToRemoveFromRotation.toString)
    updateConfig(ConfigParams.ProposalRatioToLeaveVoting -> ratioToRemoveFromRotation.toString)

    OkApiResult(Some(CalculateProposalThresholdsResult()))
  }
}

