package controllers.domain.app.quest

import components._
import controllers.domain.{DomainAPIComponent, _}
import controllers.domain.app.user._
import controllers.domain.helpers._
import models.domain.common.ContentVote
import models.domain.quest.{Quest, QuestStatus}
import play.Logger

case class UpdateQuestStatusRequest(quest: Quest)
case class UpdateQuestStatusResult(quest: Quest)

case class SelectQuestToTimeLineRequest(quest: Quest)
case class SelectQuestToTimeLineResult()

case class SolveQuestUpdateRequest(quest: Quest, ratio: Int, solutionId: String)
case class SolveQuestUpdateResult()

case class VoteQuestRequest(
  quest: Quest,
  vote: ContentVote.Value)
case class VoteQuestResult(quest: Quest)

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
            InternalErrorApiResult.apply(s"Unable to find author of quest user $authorId")
          case Some(author) =>
            rewardQuestAuthor(RewardQuestAuthorRequest(q, author))
        }
      }

      OkApiResult(UpdateQuestStatusResult(q))
    }
  }

  /**
   * Quest was randomly selected for time line, update its stats accordingly
   */
  def selectQuestToTimeLine(request: SelectQuestToTimeLineRequest): ApiResult[SelectQuestToTimeLineResult] = handleDbException {
    import request._

    {
      db.quest.updatePoints(quest.id, timelinePointsChange = -1)
    } ifSome { v =>
      updateQuestStatus(UpdateQuestStatusRequest(v))
    } map {
      OkApiResult(SelectQuestToTimeLineResult())
    }
  }

  /**
   * Update quest if someone solves it.
   */
  def solveQuestUpdate(request: SolveQuestUpdateRequest): ApiResult[SolveQuestUpdateResult] = handleDbException {
    import request._
    {
      runWhileSome(quest)(
      { q: Quest =>
        db.quest.updatePoints(
          id = quest.id,
          timelinePointsChange = ratio)
      }, { q: Quest =>
        db.quest.addSolution(q.id)
      }) ifSome { v =>
        updateQuestStatus(UpdateQuestStatusRequest(v))
      } map {
        OkApiResult(SolveQuestUpdateResult())
      }
    }
  }

  /**
   * Updates quest according to vote.
   */
  def voteQuest(request: VoteQuestRequest): ApiResult[VoteQuestResult] = handleDbException {
    import ContentVote._
    import request._

    def checkInc[T](v: T, c: T, inc: Int = 1, n: Int = 0) = if (v == c) n + inc else n

    db.quest.updatePoints(
      id = quest.id,
      timelinePointsChange =
        checkInc(vote, Cool, 1) +
        checkInc(vote, Cheating, -5) +
        checkInc(vote, IASpam, -10) +
        checkInc(vote, IAPorn, -10),
      likesChange = checkInc(vote, Cool),
      votersCountChange = 1,
      cheatingChange = checkInc(vote, Cheating),
      spamChange = checkInc(vote, IASpam),
      pornChange = checkInc(vote, IAPorn)) ifSome
    { q =>
      updateQuestStatus(UpdateQuestStatusRequest(q))
    } map { r =>
      OkApiResult(VoteQuestResult(r.quest))
    }
  }
}

