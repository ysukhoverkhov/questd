package controllers.domain.app.user

import controllers.domain.app.quest.GetMyQuestsRequest
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

case class StoreProposalInDailyResultRequest(user: User, quest: Quest, reward: Option[Assets] = None, penalty: Option[Assets] = None)
case class StoreProposalInDailyResultResult(user: User)

case class StoreSolutionInDailyResultRequest(user: User, solution: QuestSolution, reward: Option[Assets] = None, penalty: Option[Assets] = None)
case class StoreSolutionInDailyResultResult(user: User)


private[domain] trait DailyResultAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Shifts daily result.
   */
  def shiftDailyResult(request: ShiftDailyResultRequest): ApiResult[ShiftDailyResultResult] = handleDbException {
    import request._

    val dailySalary = user.dailySalary

    getMyQuests(GetMyQuestsRequest(
      user = user,
      status = QuestStatus.InRotation
    )) ifOk { r =>
      val questsIncome = r.quests.map(q => QuestsIncome(
        questId = q.id,
        passiveIncome = q.dailyPassiveIncome,
        timesLiked = q.rating.likesCount,
        likesIncome = q.dailyIncomeForLikes
        )).toList

      db.user.addPrivateDailyResult(
        user.id,
        DailyResult(
          user.getStartOfCurrentDailyResultPeriod,
          dailySalary,
          questsIncome = questsIncome)) ifSome { u =>

        OkApiResult(ShiftDailyResultResult(u))
      }
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

        assetsAfterSolutions
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

    val qpr = QuestProposalResult(
      questId = request.quest.id,
      reward = request.reward,
      penalty = request.penalty,
      status = request.quest.status)

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

    val qpr = QuestSolutionResult(
      solutionId = request.solution.id,
      reward = request.reward,
      penalty = request.penalty,
      status = request.solution.status)

    db.user.storeSolutionInDailyResult(user.id, qpr) ifSome { v =>
      OkApiResult(StoreSolutionInDailyResultResult(v))
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


