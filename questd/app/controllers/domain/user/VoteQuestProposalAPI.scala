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

case class GetQuestToVoteRequest(user: User)
case class GetQuestToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestRequest(user: User, vote: QuestProposalVote.Value, duration: Option[QuestDuration.Value] = None, difficulty: Option[QuestDifficulty.Value] = None)
case class VoteQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None)

private[domain] trait VoteQuestProposalAPI { this: DBAccessor =>

  /**
   * Get cost of quest to shuffle.
   */
  def getQuestToVote(request: GetQuestToVoteRequest): ApiResult[GetQuestToVoteResult] = handleDbException {
    import request._

    user.canGetQuestForVote match {
      case OK => {

        // Updating user profile.
        val q = user.getQuestToVote

        q match {
          case None => OkApiResult(Some(GetQuestToVoteResult(OutOfContent)))
          case Some(a) => {
            val qi = Some(QuestInfoWithID(a.id, a.info))

            val u = user.copy(
              profile = user.profile.copy(
                questProposalVoteContext = user.profile.questProposalVoteContext.copy(
                  reviewingQuest = qi)))

            db.user.update(u)

            OkApiResult(Some(GetQuestToVoteResult(OK, Some(u.profile))))
          }
        }

      }
      case a => OkApiResult(Some(GetQuestToVoteResult(a)))
    }

  }

  /**
   * Get cost of quest to shuffle.
   */
  def voteQuestProposal(request: VoteQuestRequest): ApiResult[VoteQuestResult] = handleDbException {
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

        q2.copy(rating = q.rating.copy(
          difficultyRating = q.rating.difficultyRating.copy(
            easy = checkInc(dif.get, QuestDifficulty.Easy, q2.rating.difficultyRating.easy),
            normal = checkInc(dif.get, QuestDifficulty.Normal, q2.rating.difficultyRating.normal),
            hard = checkInc(dif.get, QuestDifficulty.Normal, q2.rating.difficultyRating.hard),
            extreme = checkInc(dif.get, QuestDifficulty.Normal, q2.rating.difficultyRating.extreme)),
          durationRating = q.rating.durationRating.copy(
            mins = checkInc(dur.get, QuestDuration.Minutes, q2.rating.durationRating.mins),
            hour = checkInc(dur.get, QuestDuration.Hours, q2.rating.durationRating.hour),
            day = checkInc(dur.get, QuestDuration.Day, q2.rating.durationRating.day),
            days = checkInc(dur.get, QuestDuration.TwoDays, q2.rating.durationRating.days),
            week = checkInc(dur.get, QuestDuration.Week, q2.rating.durationRating.week))))
      } else {
        q2
      }
    }

    user.canVoteQuest match {
      case OK => {
        // 1. get quest to vote.
        // 2. update quest params.
        // 3. check change quest state
        // 4. save quest in db.
        db.quest.readByID(user.profile.questProposalVoteContext.reviewingQuest.get.id) match {
          case None => Logger.error("Unable to find quest with id for voting " + user.profile.questProposalVoteContext.reviewingQuest.get.id)
          case Some(q) => {
            db.quest.update(updateQuestWithVote(q, vote, duration, difficulty).updateStatus)
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

        OkApiResult(Some(VoteQuestResult(OK, Some(u.profile), Some(reward))))

      }
      case a => OkApiResult(Some(VoteQuestResult(a)))
    }
  }

}


