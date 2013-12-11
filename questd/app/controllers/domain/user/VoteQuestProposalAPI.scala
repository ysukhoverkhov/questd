package controllers.domain.user

import components.DBAccessor
import controllers.domain.ApiResult
import controllers.domain.OkApiResult
import controllers.domain.helpers.exceptionwrappers.handleDbException
import logic._
import models.domain._
import models.domain.base.QuestInfoWithID
import protocol.ProfileModificationResult.OK
import protocol.ProfileModificationResult.OutOfContent
import protocol.ProfileModificationResult.ProfileModificationResult
import play.Logger

case class GetQuestProposalToVoteRequest(user: User)
case class GetQuestProposalToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestProposalRequest(user: User, vote: QuestProposalVote.Value, duration: Option[QuestDuration.Value] = None, difficulty: Option[QuestDifficulty.Value] = None)
case class VoteQuestProposalResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None)

private[domain] trait VoteQuestProposalAPI { this: DBAccessor =>

  /**
   * Get cost of quest to shuffle.
   */
  def getQuestProposalToVote(request: GetQuestProposalToVoteRequest): ApiResult[GetQuestProposalToVoteResult] = handleDbException {
    import request._

    user.canGetQuestProposalForVote match {
      case OK => {

        // Updating user profile.
        val q = user.getQuestProposalToVote

        q match {
          case None => OkApiResult(Some(GetQuestProposalToVoteResult(OutOfContent)))
          case Some(a) => {
            val qi = Some(QuestInfoWithID(a.id, a.info))

            val u = user.copy(
              profile = user.profile.copy(
                questProposalVoteContext = user.profile.questProposalVoteContext.copy(
                  reviewingQuest = qi)))

            db.user.update(u)

            OkApiResult(Some(GetQuestProposalToVoteResult(OK, Some(u.profile))))
          }
        }

      }
      case a => OkApiResult(Some(GetQuestProposalToVoteResult(a)))
    }

  }

  /**
   * Get cost of quest to shuffle.
   */
  def voteQuestProposal(request: VoteQuestProposalRequest): ApiResult[VoteQuestProposalResult] = handleDbException {
    import request._

    def updateQuestWithVote(q: Quest, v: QuestProposalVote.Value, dur: Option[QuestDuration.Value], dif: Option[QuestDifficulty.Value]): Quest = {
      import QuestProposalVote._

      def checkInc[T](v: T, c: T, n: Int) = if (v == c) n + 1 else n

      val q2 = q.copy(
        rating = q.rating.copy(
          votersCount = q.rating.votersCount + 1,
          points = checkInc(v, Cool, q.rating.points),
          cheating = checkInc(v, Cheating, q.rating.cheating),
          iacpoints = q.rating.iacpoints.copy(
            spam = checkInc(v, IASpam, q.rating.iacpoints.spam),
            porn = checkInc(v, IAPorn, q.rating.iacpoints.porn))))

      if (v == QuestProposalVote.Cool) {

        q2.copy(rating = q2.rating.copy(
          difficultyRating = q2.rating.difficultyRating.copy(
            easy = checkInc(dif.get, QuestDifficulty.Easy, q2.rating.difficultyRating.easy),
            normal = checkInc(dif.get, QuestDifficulty.Normal, q2.rating.difficultyRating.normal),
            hard = checkInc(dif.get, QuestDifficulty.Normal, q2.rating.difficultyRating.hard),
            extreme = checkInc(dif.get, QuestDifficulty.Normal, q2.rating.difficultyRating.extreme)),
          durationRating = q2.rating.durationRating.copy(
            mins = checkInc(dur.get, QuestDuration.Minutes, q2.rating.durationRating.mins),
            hour = checkInc(dur.get, QuestDuration.Hours, q2.rating.durationRating.hour),
            day = checkInc(dur.get, QuestDuration.Day, q2.rating.durationRating.day),
            days = checkInc(dur.get, QuestDuration.TwoDays, q2.rating.durationRating.days),
            week = checkInc(dur.get, QuestDuration.Week, q2.rating.durationRating.week))))
      } else {
        q2
      }
    }

    def rewardQuestProposalAuthor(q: Quest): Unit = {
      db.user.readByID(q.userID) match {
        case None => Logger.error("Unable to find author of quest user " + q.userID)
        case Some(author) => {
          val u: User = QuestStatus.withName(q.status) match {
            case QuestStatus.OnVoting => {
              Logger.error("We are rewarding player for proposal what is on voting.")
              author
            }
            case QuestStatus.InRotation => author.giveUserReward(author.rewardForMakingQuest)
            case QuestStatus.RatingBanned => author
            case QuestStatus.CheatingBanned => author.giveUserPenalty(author.penaltyForCheating)
            case QuestStatus.IACBanned => author.giveUserPenalty(author.penaltyForIAC)
            case QuestStatus.OldBanned => author
          }
          
          db.user.update(u)
        }
      }

    }
    
    // TODO everywhere replace direct giving of reward to player with call to a single function in logic (rename assets field to find them).

    user.canVoteQuestProposal match {
      case OK => {
        // 1. get quest to vote.
        // 2. update quest params.
        // 3. check change quest state
        // 4. save quest in db.
        db.quest.readByID(user.profile.questProposalVoteContext.reviewingQuest.get.id) match {
          case None => Logger.error("Unable to find quest with id for voting " + user.profile.questProposalVoteContext.reviewingQuest.get.id)
          case Some(q) => {
            val updatedQuest = updateQuestWithVote(q, vote, duration, difficulty).updateStatus

            if (updatedQuest.status != q.status) {
              rewardQuestProposalAuthor(updatedQuest)
            }

            db.quest.update(updatedQuest)
          }
        }

        // 5. update user profile.
        // 6. save profile in db.
        val reward = user.getQuestProposalVoteReward

        val u = user.copy(
          profile = user.profile.copy(
            questProposalVoteContext = user.profile.questProposalVoteContext.copy(
              numberOfReviewedQuests = user.profile.questProposalVoteContext.numberOfReviewedQuests + 1,
              reviewingQuest = None),
            assets = user.profile.assets + reward))

        db.user.update(u)

        OkApiResult(Some(VoteQuestProposalResult(OK, Some(u.profile), Some(reward))))

      }
      case a => OkApiResult(Some(VoteQuestProposalResult(a)))
    }
  }

}


