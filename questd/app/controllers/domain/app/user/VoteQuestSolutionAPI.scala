package controllers.domain.app.user

import play.Logger
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers.handleDbException
import logic._
import models.domain._
import models.domain.view._
import models.domain.base._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.DomainAPIComponent
import controllers.domain.InternalErrorApiResult
import controllers.domain.app.questsolution.VoteQuestSolutionUpdateRequest
import controllers.domain.InternalErrorApiResult

case class GetQuestSolutionToVoteRequest(user: User)
case class GetQuestSolutionToVoteResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class VoteQuestSolutionRequest(user: User, vote: QuestSolutionVote.Value)
case class VoteQuestSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None, reward: Option[Assets] = None, solver: Option[PublicProfileWithID] = None)

private[domain] trait VoteQuestSolutionAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

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
            val qsi = QuestSolutionInfoWithID(a.id, a.info)
            val questInfo = db.quest.readById(a.info.questId).map(_.info)

            if (questInfo == None) {
              Logger.error("API - getQuestSolutionToVote. Unable to find quest for solution.")
              InternalErrorApiResult()
            } else {
              val u = db.user.selectQuestSolutionVote(user.id, qsi, questInfo.get)
              OkApiResult(Some(GetQuestSolutionToVoteResult(OK, u.map(_.profile))))
            }
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

    Logger.debug("API - voteQuestSolution")

    request.user.canVoteQuestSolution match {
      case OK => {
        // 1. get quest to vote.
        // 2. update quest params.
        // 3. check change quest state
        // 4. save quest in db.
        val reward = request.user.getQuestSolutionVoteReward

        db.solution.readById(request.user.profile.questSolutionVoteContext.reviewingQuestSolution.get.id) match {
          case None => {
            Logger.error("Unable to find quest solution with id for voting " + request.user.profile.questSolutionVoteContext.reviewingQuestSolution.get.id)
            InternalErrorApiResult()
          }
          case Some(s) => {
            {
              voteQuestSolutionUpdate(VoteQuestSolutionUpdateRequest(s, request.vote))
            } map {

              // 5. update user profile.
              // 6. save profile in db.
              adjustAssets(AdjustAssetsRequest(user = request.user, reward = Some(reward))) map { r =>

                val u = db.user.recordQuestSolutionVote(r.user.id, s.id)

                val solver = if (request.vote == QuestSolutionVote.Cool) {
                  // TODO: make here ifSome
                  val a = db.user.readById(s.userId)
                  if (a != None) {
                    Some(PublicProfileWithID(a.get.id, a.get.profile.publicProfile))
                  } else {
                    None
                  }
                } else {
                  None
                }

                OkApiResult(Some(VoteQuestSolutionResult(OK, u.map(_.profile), Some(reward), solver)))
              }
            }
          }
        }
      }

      case a => OkApiResult(Some(VoteQuestSolutionResult(a)))
    }
  }

}


