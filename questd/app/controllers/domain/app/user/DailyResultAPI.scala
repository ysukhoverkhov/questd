package controllers.domain.app.user

import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import play.Logger

case class ShiftDailyResultRequest(user: User)
case class ShiftDailyResultResult(user: User)

case class GetDailyResultRequest(user: User)
case class GetDailyResultResult(profile: Profile, hasNewResult: Boolean)

case class StoreProposalInDailyResultRequest(user: User, questId: String, reward: Option[Assets] = None, penalty: Option[Assets] = None)
case class StoreProposalInDailyResultResult(user: User)

case class StoreSolutionInDailyResultRequest(user: User, solutionId: String, reward: Option[Assets] = None, penalty: Option[Assets] = None)
case class StoreSolutionInDailyResultResult(user: User)

case class StoreProposalOutOfTimePenaltyReqest(user: User, penalty: Assets)
case class StoreProposalOutOfTimePenaltyResult(user: User)

case class StoreSolutionOutOfTimePenaltyReqest(user: User, penalty: Assets)
case class StoreSolutionOutOfTimePenaltyResult(user: User)

private[domain] trait DailyResultAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Shifts daily result.
   */
  def shiftDailyResult(request: ShiftDailyResultRequest): ApiResult[ShiftDailyResultResult] = handleDbException {
    import request._

    val dailyAssetsDecrease = user.dailyAssetsDecrease

    val u = db.user.addPrivateDailyResult(user.id, DailyResult(user.getStartOfCurrentDailyResultPeriod, dailyAssetsDecrease))

    u match {
      case Some(u: User) => OkApiResult(ShiftDailyResultResult(u))
      case _ =>
        Logger.error("API - shiftDailyResult. user is not in db after update.")
        InternalErrorApiResult()
    }
  }

  /**
   * Returns moves ready daily results to public daily results and returns public results to client
   */
  def getDailyResult(request: GetDailyResultRequest): ApiResult[GetDailyResultResult] = handleDbException {

    def applyDailyResults(u: User) = {

      val deltaAssets = u.profile.dailyResults.foldLeft(Assets()) { (a, dr) =>

        val assetsAfterProposals = dr.decidedQuestProposals.foldLeft(a) { (a, dqp) =>
          a + dqp.reward.getOrElse(Assets()) - dqp.penalty.getOrElse(Assets())
        }

        val assetsAfterSolutions = dr.decidedQuestSolutions.foldLeft(assetsAfterProposals) { (a, dqs) =>
          a + dqs.reward.getOrElse(Assets()) - dqs.penalty.getOrElse(Assets())
        }

        assetsAfterSolutions -
          dr.dailyAssetsDecrease -
          dr.questGiveUpAssetsDecrease.getOrElse(Assets()) -
          dr.proposalGiveUpAssetsDecrease.getOrElse(Assets())
      }

      adjustAssets(AdjustAssetsRequest(user = u, reward = Some(deltaAssets)))
    }

    // Check replace old public daily results with new daily results.
    val (u, newOne, internalError) = if (request.user.privateDailyResults.length > 1) {
      db.user.movePrivateDailyResultsToPublic(request.user.id, request.user.privateDailyResults.tail) match {
        case Some(us) =>
          applyDailyResults(us)
          (us, true, false)

        case None =>
          Logger.error("API - getDailyResult. Unable to find user for getting daily result")
          (request.user, false, true)
      }

    } else {
      (request.user, false, false)
    }

    if (internalError) {
      InternalErrorApiResult()
    } else {
      OkApiResult(GetDailyResultResult(u.profile, newOne))
    }
  }

  /**
   * Stores result of voting of quest proposal in db
   */
  def storeProposalInDailyResult(request: StoreProposalInDailyResultRequest): ApiResult[StoreProposalInDailyResultResult] = handleDbException({
    import request._

    val u = ensurePrivateDailyResultExists(user)

    val qpr = QuestProposalResult(questProposalId = request.questId, reward = request.reward, penalty = request.penalty)
    db.user.storeProposalInDailyResult(user.id, qpr) ifSome { v =>
      OkApiResult(StoreProposalInDailyResultResult(v))
    }
  })

  /**
   * Stores result of voting of quest solution in db
   */
  def storeSolutionInDailyResult(request: StoreSolutionInDailyResultRequest): ApiResult[StoreSolutionInDailyResultResult] = handleDbException {
    import request._

    val u = ensurePrivateDailyResultExists(user)

    val qpr = QuestSolutionResult(questSolutionId = request.solutionId, reward = request.reward, penalty = request.penalty)
    db.user.storeSolutionInDailyResult(user.id, qpr) ifSome { v =>
      OkApiResult(StoreSolutionInDailyResultResult(v))
    }

  }

  def storeProposalOutOfTimePenalty(request: StoreProposalOutOfTimePenaltyReqest): ApiResult[StoreProposalOutOfTimePenaltyResult] = handleDbException {
    import request._

    val u = ensurePrivateDailyResultExists(user)

    db.user.storeProposalOutOfTimePenalty(user.id, penalty) ifSome { v =>
      OkApiResult(StoreProposalOutOfTimePenaltyResult(v))
    }
  }

  def storeSolutionOutOfTimePenalty(request: StoreSolutionOutOfTimePenaltyReqest): ApiResult[StoreSolutionOutOfTimePenaltyResult] = handleDbException {
    import request._

    val u = ensurePrivateDailyResultExists(user)

    db.user.storeSolutionOutOfTimePenalty(user.id, penalty) ifSome { v =>
      OkApiResult(StoreSolutionOutOfTimePenaltyResult(v))
    }

  }

  private def ensurePrivateDailyResultExists(user: User): User = {
    if (user.privateDailyResults.length == 0) {
      shiftDailyResult(ShiftDailyResultRequest(user)).body.get.user
    } else {
      user
    }
  }

}


