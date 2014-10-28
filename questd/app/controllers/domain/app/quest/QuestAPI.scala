package controllers.domain.app.quest

import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.app.user._
import controllers.domain.helpers._
import play.Logger

case class UpdateQuestStatusRequest(quest: Quest)
case class UpdateQuestStatusResult()

case class SelectQuestToTimeLineRequest(quest: Quest)
case class SelectQuestToTimeLineResult()

case class SolveQuestUpdateRequest(quest: Quest, ratio: Int)
case class SolveQuestUpdateResult()

case class VoteQuestRequest(
  quest: Quest,
  vote: ContentVote.Value)
case class VoteQuestResult()

case class CalculateProposalThresholdsRequest(proposalsVoted: Double, proposalsLiked: Double)
case class CalculateProposalThresholdsResult()

private[domain] trait QuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates quest status taking votes into account.
   */ // TODO: implement me.
  def updateQuestStatus(request: UpdateQuestStatusRequest): ApiResult[UpdateQuestStatusResult] = handleDbException {
    import request._

    def capPoints(quest: Quest): Option[Quest] = {
      if (quest.rating.votersCount > Int.MaxValue / 2) {
        Logger.error("quest.rating.votersCount > Int.MaxValue / 2. this is the time to invent what to do with this.")
      }

      Some(quest)
    }

//    def checkAddToRotation(quest: Quest): Option[Quest] = {
//      if (quest.shouldAddToRotation) {
//        db.quest.updateStatus(quest.id, QuestStatus.InRotation.toString)
//        db.quest.updateInfo(
//            quest.id,
//            1 // TODO: get author level here.
//            )
//      } else
//        Some(quest)
//    }

//    def checkRemoveFromRotation(quest: Quest): Option[Quest] = {
//      if (quest.shouldRemoveFromRotation)
//        db.quest.updateStatus(quest.id, QuestStatus.RatingBanned.toString)
//      else
//        Some(quest)
//    }

    def checkBanQuest(quest: Quest): Option[Quest] = {
      if (quest.shouldBanIAC)
        db.quest.updateStatus(quest.id, QuestStatus.IACBanned.toString)
      else
        Some(quest)
    }

    def checkCheatingQuest(quest: Quest): Option[Quest] = {
      if (quest.shouldBanCheating)
        db.quest.updateStatus(quest.id, QuestStatus.CheatingBanned.toString)
      else
        Some(quest)
    }

//    def checkRemoveQuestFromVotingByTime(quest: Quest): Option[Quest] = {
//      if (quest.shouldRemoveQuestFromVotingByTime)
//        db.quest.updateStatus(quest.id, QuestStatus.OldBanned.toString)
//      else
//        Some(quest)
//    }

    val funcs = List(
//      checkRemoveQuestFromVotingByTime _,
      checkCheatingQuest _,
      checkBanQuest _,
//      checkRemoveFromRotation _,
//      checkAddToRotation _,
      capPoints _)

    val updatedQuest = funcs.foldLeft[Option[Quest]](Some(quest))((r, f) => {
      r.flatMap(f)
    })

    updatedQuest ifSome { q =>
      if (q.status != quest.status) {
        val authorId = quest.info.authorId
        db.user.readById(authorId) match {
          case None =>
            Logger.error("Unable to find author of quest user " + authorId)
            InternalErrorApiResult()
          case Some(author) =>
            rewardQuestProposalAuthor(RewardQuestProposalAuthorRequest(q, author))
        }
      }

      OkApiResult(UpdateQuestStatusResult())
    }
  }

  /**
   * Quest was randomly selected for time line, update its stats accordingly
   */
  def selectQuestToTimeLine(request: SelectQuestToTimeLineRequest): ApiResult[SelectQuestToTimeLineResult] = handleDbException {
    import request._

    {
      db.quest.updatePoints(quest.id, -1)
    } ifSome { v =>
      updateQuestStatus(UpdateQuestStatusRequest(v))
    } ifOk {
      OkApiResult(SelectQuestToTimeLineResult())
    }
  }

  /**
   * Update quest if someone solves it.
   */
  def solveQuestUpdate(request: SolveQuestUpdateRequest): ApiResult[SolveQuestUpdateResult] = handleDbException {
    import request._

    {
      db.quest.updatePoints(quest.id, ratio, 1)
    } ifSome { v =>
      updateQuestStatus(UpdateQuestStatusRequest(v))
    } ifOk {
      OkApiResult(SolveQuestUpdateResult())
    }
  }

  /**
   * Updates quest according to vote.
   */
  def voteQuest(request: VoteQuestRequest): ApiResult[VoteQuestResult] = handleDbException {
    import request._
    import models.domain.ContentVote._

    def checkInc[T](v: T, c: T, n: Int = 0) = if (v == c) n + 1 else n

    val q = db.quest.updatePoints(
      quest.id,
      checkInc(vote, Cool),
      1,
      checkInc(vote, Cheating),
      checkInc(vote, IASpam),
      checkInc(vote, IAPorn))

    q ifSome { v =>
      updateQuestStatus(UpdateQuestStatusRequest(v))
    } ifOk {
      OkApiResult(VoteQuestResult())
    }
}

  def calculateProposalThresholds(request: CalculateProposalThresholdsRequest): ApiResult[CalculateProposalThresholdsResult] = handleDbException {

    // TODO: clean me up.
//    val proposalsOnVoting = Math.max(1, db.quest.countWithStatus(QuestStatus.OnVoting.toString))
//    val daysForQuestToEnter: Long = config(ConfigParams.ProposalNormalDaysToEnterRotation).toInt
//    val likesToAddToRotation: Long = Math.round(request.proposalsLiked / proposalsOnVoting * daysForQuestToEnter)
//    val votesToRemoveFromRotation: Long = Math.max(
//      Math.round(request.proposalsVoted / proposalsOnVoting * daysForQuestToEnter),
//      config(ConfigParams.ProposalMinVotesToTakeRemovalDecision).toInt)
//    val ratioToRemoveFromRotation: Double = (request.proposalsLiked / request.proposalsVoted) * config(ConfigParams.ProposalWorstLikesRatio).toDouble
//
//    Logger.info("Calculating proposals threshold")
//    Logger.info(
//      s"  likesToAddToRotation = $likesToAddToRotation, proposalsLiked during last week = ${request.proposalsLiked}, proposalsOnVoting now = $proposalsOnVoting, daysForQuestToEnter = $daysForQuestToEnter")
//    Logger.info(
//      s"  votesToRemoveFromRotation = $votesToRemoveFromRotation")
//    Logger.info(
//      s"  ratioToRemoveFromRotation = $ratioToRemoveFromRotation")
//
//    updateConfig(ConfigParams.ProposalLikesToEnterRotation -> likesToAddToRotation.toString)
//    updateConfig(ConfigParams.ProposalVotesToLeaveVoting -> votesToRemoveFromRotation.toString)
//    updateConfig(ConfigParams.ProposalRatioToLeaveVoting -> ratioToRemoveFromRotation.toString)

    OkApiResult(CalculateProposalThresholdsResult())
  }
}

