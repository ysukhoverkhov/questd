package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger

case class ShiftDailyResultRequest(user: User)
case class ShiftDailyResultResult(user: User)

case class GetDailyResultRequest(user: User)
case class GetDailyResultResult(profile: Profile, hasNewResult: Boolean)

case class StoreProposalInDailyResultRequest(user: User, questId: String, reward: Option[Assets] = None, penalty: Option[Assets] = None)
case class StoreProposalInDailyResultResult(user: User)

case class StoreSolutionInDailyResultRequest(user: User, solutionId: String, reward: Option[Assets] = None, penalty: Option[Assets] = None)
case class StoreSolutionInDailyResultResult(user: User)

private[domain] trait DailyResultAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Shifts daily result.
   */
  def shiftDailyResult(request: ShiftDailyResultRequest): ApiResult[ShiftDailyResultResult] = handleDbException {
    import request._

    val dailyAssetsDecrease = user.dailyAssetsDecrease

    //    val u = user.copy(privateDailyResults = DailyResult(user.getStartOfCurrentDailyResultPeriod, dailyAssetsDecrease) :: user.privateDailyResults)
    val u = db.user.addPrivateDailyResult(user.id, DailyResult(user.getStartOfCurrentDailyResultPeriod, dailyAssetsDecrease))

    u match {
      case Some(u: User) => OkApiResult(Some(ShiftDailyResultResult(u)))
      case _ => {
        Logger.error("API - shiftDailyResult. user is not in db after update.")
        InternalErrorApiResult()
      }
    }
  }

  /**
   * Returns moves ready daily results to public daily results and returns public results to client
   */
  def getDailyResult(request: GetDailyResultRequest): ApiResult[GetDailyResultResult] = handleDbException {
Logger.error("Fu 2")
    def applyDailyResults(u: User) = {

      val deltaAssets = u.profile.dailyResults.foldLeft(Assets()) { (a, dr) =>

        val assetsAfterProposals = dr.decidedQuestProposals.foldLeft(a) { (a, dqp) =>
          a + dqp.reward.getOrElse(Assets()) - dqp.penalty.getOrElse(Assets())
        }

        val assetsAfterSolutions = dr.decidedQuestSolutions.foldLeft(assetsAfterProposals) { (a, dqs) =>
          a + dqs.reward.getOrElse(Assets()) - dqs.penalty.getOrElse(Assets())
        }

        assetsAfterSolutions - dr.dailyAssetsDecrease
      }

      adjustAssets(AdjustAssetsRequest(user = u, reward = Some(deltaAssets)))
    }

    // Check replace old public daily results with new daily results.
    val (u, newOne) = if (request.user.privateDailyResults.length > 1) {
      val u = db.user.movePrivateDailyResultsToPublic(request.user.id, request.user.privateDailyResults.tail)

      if (u != None) {
        applyDailyResults(u.get)
        (u.get, true)
      } else {
        Logger.error("API - getDailyResult. Unable to find user for getting daily result")
        (request.user, false)
      }

    } else {
      (request.user, false)
    }

    OkApiResult(Some(GetDailyResultResult(u.profile, newOne)))
  }

  /**
   * Stores result of voting of quest proposal in db
   */
  def storeProposalInDailyResult(request: StoreProposalInDailyResultRequest): ApiResult[StoreProposalInDailyResultResult] = handleDbException {
    import request._

    val u = if (user.privateDailyResults.length == 0) {
      shiftDailyResult(ShiftDailyResultRequest(user)).body.get.user
    } else {
      request.user
    }

    val qpr = QuestProposalResult(questProposalId = request.questId, reward = request.reward, penalty = request.penalty)
    val u2 = db.user.storeProposalInDailyResult(user.id, qpr)

    OkApiResult(Some(StoreProposalInDailyResultResult(u2.get)))
  }

  /**
   * Stores result of voting of quest solution in db
   */
  def storeSolutionInDailyResult(request: StoreSolutionInDailyResultRequest): ApiResult[StoreSolutionInDailyResultResult] = handleDbException {
    import request._

    val u = if (user.privateDailyResults.length == 0) {
      shiftDailyResult(ShiftDailyResultRequest(user)).body.get.user
    } else {
      request.user
    }

    val qpr = QuestSolutionResult(questSolutionId = request.solutionId, reward = request.reward, penalty = request.penalty)
    val u2 = db.user.storeSolutionInDailyResult(user.id, qpr)

    OkApiResult(Some(StoreSolutionInDailyResultResult(u2.get)))
  }
}


