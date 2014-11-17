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
   */
  def updateQuestStatus(request: UpdateQuestStatusRequest): ApiResult[UpdateQuestStatusResult] = handleDbException {
    import request._

    def capPoints(quest: Quest): Option[Quest] = {
      if (quest.rating.votersCount > Int.MaxValue / 2) {
        Logger.error("quest.rating.votersCount > Int.MaxValue / 2. this is the time to invent what to do with this.")
      }

      Some(quest)
    }

    def checkBanQuest(quest: Quest): Option[Quest] = {
      if (quest.shouldBanIAC)
        db.quest.updateStatus(quest.id, QuestStatus.IACBanned)
      else
        Some(quest)
    }

    def checkCheatingQuest(quest: Quest): Option[Quest] = {
      if (quest.shouldBanCheating)
        db.quest.updateStatus(quest.id, QuestStatus.CheatingBanned)
      else
        Some(quest)
    }

    val functions = List(
      checkCheatingQuest _,
      checkBanQuest _,
      capPoints _)

    val updatedQuest = functions.foldLeft[Option[Quest]](Some(quest))((r, f) => {
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
            rewardQuestAuthor(RewardQuestAuthorRequest(q, author))
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
}

