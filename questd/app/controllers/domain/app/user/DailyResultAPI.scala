package controllers.domain.app.user

import controllers.domain.app.quest.GetMyQuestsRequest
import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import play.Logger
import logic.constants._

case class ShiftDailyResultRequest(user: User)
case class ShiftDailyResultResult(user: User)

case class GetDailyResultRequest(user: User)
case class GetDailyResultResult(profile: Profile, hasNewResult: Boolean)

case class StoreQuestSolvingInDailyResultRequest(user: User, quest: Quest)
case class StoreQuestSolvingInDailyResultResult(user: User)

case class AddQuestIncomeToDailyResultRequest(user: User, quest: Quest)
case class AddQuestIncomeToDailyResultResult(user: User)

case class RemoveQuestIncomeFromDailyResultRequest(user: User, questId: String)
case class RemoveQuestIncomeFromDailyResultResult(user: User)

case class StoreProposalInDailyResultRequest(user: User, quest: Quest, reward: Option[Assets] = None, penalty: Option[Assets] = None)
case class StoreProposalInDailyResultResult(user: User)

case class StoreSolutionInDailyResultRequest(user: User, solution: Solution, reward: Option[Assets] = None, penalty: Option[Assets] = None)
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
      val questsIncome = r.quests.map(q => createQuestIncomeForQuest(q)).toList

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

        val assetsAfterSalary = a + dr.dailySalary

        val assetsAfterProposals = dr.decidedQuestProposals.foldLeft(assetsAfterSalary) { (a, dqp) =>
          a + dqp.reward.getOrElse(Assets()) - dqp.penalty.getOrElse(Assets())
        }

        val assetsAfterSolutions = dr.decidedQuestSolutions.foldLeft(assetsAfterProposals) { (a, dqs) =>
          a + dqs.reward.getOrElse(Assets()) - dqs.penalty.getOrElse(Assets())
        }

        dr.questsIncome.foldLeft(assetsAfterSolutions) { (a, dqi) =>
          a + dqi.likesIncome + dqi.passiveIncome + dqi.solutionsIncome
        }
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
   * Stores information about our solved quest in our daily results. This assumes reward.
   * @param request Request for the task.
   * @return Response of the task.
   */
  def storeQuestSolvingInDailyResult(request: StoreQuestSolvingInDailyResultRequest): ApiResult[StoreQuestSolvingInDailyResultResult] = handleDbException {
    import request._

    val u = ensurePrivateDailyResultExists(user)

    u.privateDailyResults.head.questsIncome.find(_.questId == quest.id) match {

      case Some(dailyResultEntry) =>
        val reward = if(dailyResultEntry.timesSolved < MaxRewardedQuestSolutionsPerDay)
          quest.rewardForSolution
        else
          Assets()

        db.user.storeQuestSolvingInDailyResult(
          u.id,
          quest.id,
          reward
        ) ifSome { updatedUser =>
          OkApiResult(StoreQuestSolvingInDailyResultResult(updatedUser))
        }

      case None =>
        OkApiResult(StoreQuestSolvingInDailyResultResult(u))
    }
  }

  /**
   * Adds questIncome to current private daily result.
   * @param request Request containing required information.
   * @return The response.
   */
  def addQuestIncomeToDailyResult(request: AddQuestIncomeToDailyResultRequest): ApiResult[AddQuestIncomeToDailyResultResult] = handleDbException {
    import request._

    db.user.addQuestIncomeToDailyResult(user.id, createQuestIncomeForQuest(quest)) ifSome { u =>
      OkApiResult(AddQuestIncomeToDailyResultResult(u))
    }
  }

  def removeQuestIncomeFromDailyResult(request: RemoveQuestIncomeFromDailyResultRequest): ApiResult[RemoveQuestIncomeFromDailyResultResult] = handleDbException {
    import request._

    db.user.removeQuestIncomeFromDailyResult(user.id, questId) ifSome { u =>
      OkApiResult(RemoveQuestIncomeFromDailyResultResult(u))
    }
  }

  /**
   * Stores result of voting of quest proposal in db
   */
  def storeProposalInDailyResult(request: StoreProposalInDailyResultRequest): ApiResult[StoreProposalInDailyResultResult] = handleDbException {
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
  }

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

  private def createQuestIncomeForQuest(quest: Quest): QuestIncome = {
    QuestIncome(
      questId = quest.id,
      passiveIncome = quest.dailyPassiveIncome,
      timesLiked = quest.rating.likesCount,
      likesIncome = quest.dailyIncomeForLikes
    )
  }


  private def ensurePrivateDailyResultExists(user: User): User = {
    if (user.privateDailyResults.length == 0) {
      shiftDailyResult(ShiftDailyResultRequest(user)).body.get.user
    } else {
      user
    }
  }

}


