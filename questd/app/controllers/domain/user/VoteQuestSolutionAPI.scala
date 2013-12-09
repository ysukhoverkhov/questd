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
import models.domain.base._

case class GetQuestSolutionToVoteRequest(user: User)
case class GetQuestSolutionToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestSolutionRequest(user: User/*, vote: QuestSolutionVote.Value, duration: Option[QuestDuration.Value] = None, difficulty: Option[QuestDifficulty.Value] = None*/)
case class VoteQuestSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None)

private[domain] trait VoteQuestSolutionAPI { this: DBAccessor =>

  /**
   * Get quest solution to vote for.
   */
  def getQuestSolutionToVote(request: GetQuestSolutionToVoteRequest): ApiResult[GetQuestSolutionToVoteResult] = handleDbException {
    import request._

    user.canGetQuestSolutionForVote match {
      case OK => {

        // Updating user profile.
        val q = user.getQuestSolutionToVote

        q match {
          case None => OkApiResult(Some(GetQuestSolutionToVoteResult(OutOfContent)))
          case Some(a) => {
            val qi = Some(QuestSolutionInfoWithID(a.id, a.info))

            val u = user.copy(
              profile = user.profile.copy(
                questSolutionVoteContext = user.profile.questSolutionVoteContext.copy(
                  reviewingQuestSolution = qi)))

            db.user.update(u)

            OkApiResult(Some(GetQuestSolutionToVoteResult(OK, Some(u.profile))))
          }
        }

      }
      case a => OkApiResult(Some(GetQuestSolutionToVoteResult(a)))
    }
  }

  /**
   * Get cost of quest to shuffle.
   */
  def voteQuestSolution(request: VoteQuestSolutionRequest): ApiResult[VoteQuestSolutionResult] = handleDbException {
    import request._
//
//    def updateQuestWithVote(q: Quest, v: QuestSolutionVote.Value, dur: Option[QuestDuration.Value], dif: Option[QuestDifficulty.Value]): Quest = {
//      import QuestSolutionVote._
//
//      def checkInc[T](v: T, c: T, n: Int) = if (v == c) n + 1 else n
//
//      val q2 = q.copy(
//        rating = q.rating.copy(
//          votersCount = q.rating.votersCount + 1,
//          points = checkInc(v, Cool, q.rating.points),
//          cheating = checkInc(v, Cheating, q.rating.cheating),
//          iacpoints = q.rating.iacpoints.copy(
//            spam = checkInc(v, IASpam, q.rating.iacpoints.spam),
//            porn = checkInc(v, IAPorn, q.rating.iacpoints.porn))))
//
//      if (v == QuestSolutionVote.Cool) {
//
//        q2.copy(rating = q.rating.copy(
//          difficultyRating = q.rating.difficultyRating.copy(
//            easy = checkInc(dif.get, QuestDifficulty.Easy, q2.rating.difficultyRating.easy),
//            normal = checkInc(dif.get, QuestDifficulty.Normal, q2.rating.difficultyRating.normal),
//            hard = checkInc(dif.get, QuestDifficulty.Normal, q2.rating.difficultyRating.hard),
//            extreme = checkInc(dif.get, QuestDifficulty.Normal, q2.rating.difficultyRating.extreme)),
//          durationRating = q.rating.durationRating.copy(
//            mins = checkInc(dur.get, QuestDuration.Minutes, q2.rating.durationRating.mins),
//            hour = checkInc(dur.get, QuestDuration.Hours, q2.rating.durationRating.hour),
//            day = checkInc(dur.get, QuestDuration.Day, q2.rating.durationRating.day),
//            days = checkInc(dur.get, QuestDuration.TwoDays, q2.rating.durationRating.days),
//            week = checkInc(dur.get, QuestDuration.Week, q2.rating.durationRating.week))))
//      } else {
//        q2
//      }
//    }
//
//    user.canVoteQuest match {
//      case OK => {
//        // 1. get quest to vote.
//        // 2. update quest params.
//        // 3. check change quest state
//        // 4. save quest in db.
//        db.quest.readByID(user.profile.questSolutionVoteContext.reviewingQuest.get.id) match {
//          case None => Logger.error("Unable to find quest with id for voting " + user.profile.questSolutionVoteContext.reviewingQuest.get.id)
//          case Some(q) => {
//            db.quest.update(updateQuestWithVote(q, vote, duration, difficulty).updateStatus)
//          }
//        }
//
//        // 5. update user profile.
//        // 6. save profile in db.
//        val reward = user.getQuestSolutionVoteReward
//
//        val u = user.copy(
//          profile = user.profile.copy(
//            questSolutionVoteContext = user.profile.questSolutionVoteContext.copy(
//              numberOfReviewedQuests = user.profile.questSolutionVoteContext.numberOfReviewedQuests + 1,
//              reviewingQuest = None),
//            assets = user.profile.assets + reward))
//
//        db.user.update(u)
//
//        OkApiResult(Some(VoteQuestSolutionResult(OK, Some(u.profile), Some(reward))))
//
//      }
//      case a => OkApiResult(Some(VoteQuestSolutionResult(a)))
//    }
    
    
    OkApiResult(None)

  }

}


