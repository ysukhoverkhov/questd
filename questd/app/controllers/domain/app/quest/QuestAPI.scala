package controllers.domain.app.quest

import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.app.user._
import controllers.domain.helpers._
import models.domain.common.ContentVote
import models.domain.quest.{QuestStatus, Quest}
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

      OkApiResult(UpdateQuestStatusResult())
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
      db.user.readById(quest.info.authorId) ifSome { u =>
        storeQuestSolvingInDailyResult(StoreQuestSolvingInDailyResultRequest(u, quest))
      } map {
        db.quest.updatePoints(
          id = quest.id,
          timelinePointsChange = ratio) ifSome { v =>
          updateQuestStatus(UpdateQuestStatusRequest(v))
        }
      } map {
        OkApiResult(SolveQuestUpdateResult())
      }
    }
  }

  /**
   * Updates quest according to vote.
   */
  def voteQuest(request: VoteQuestRequest): ApiResult[VoteQuestResult] = handleDbException {
    import request._
    import ContentVote._

    def checkInc[T](v: T, c: T, n: Int = 0) = if (v == c) n + 1 else n

    val q = db.quest.updatePoints(
      id = quest.id,
      timelinePointsChange = checkInc(vote, Cool),
      likesChange = checkInc(vote, Cool),
      votersCountChange = 1,
      cheatingChange = checkInc(vote, Cheating),
      spamChange = checkInc(vote, IASpam),
      pornChange = checkInc(vote, IAPorn))

    q ifSome { v =>
      updateQuestStatus(UpdateQuestStatusRequest(v))
    } map {
      OkApiResult(VoteQuestResult())
    }
  }
}

