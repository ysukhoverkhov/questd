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

case class StoreSolutionInDailyResultRequest(
  user: User,
  solution: Solution,
  battle: Option[Battle] = None,
  reward: Option[Assets] = None,
  penalty: Option[Assets] = None)
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
    )) map { r =>
      r.quests.foldLeft[ApiResult[AddQuestIncomeToDailyResultResult]](OkApiResult(AddQuestIncomeToDailyResultResult(user))) {
        case (OkApiResult(_), q) =>
          addQuestIncomeToDailyResult(AddQuestIncomeToDailyResultRequest(user, q))
        case (badResult, _) =>
          badResult
      } map { r =>
        db.user.addPrivateDailyResult(
          r.user.id,
          DailyResult(
            user.getStartOfCurrentDailyResultPeriod,
            dailySalary)) ifSome { u =>

          OkApiResult(ShiftDailyResultResult(u))
        }
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

    val u = ensurePrivateDailyResultExists(user)

    if (u.privateDailyResults.head.questsIncome.exists(_.questId == quest.id)) {
      OkApiResult(AddQuestIncomeToDailyResultResult(u))
    } else {
      db.user.addQuestIncomeToDailyResult(u.id, createQuestIncomeForQuest(quest)) ifSome { u =>
        OkApiResult(AddQuestIncomeToDailyResultResult(u))
      }
    }
  }

  /**
   * Removes info about quest income from daily results since we do not need it there anymore.
   * @param request Request with all required information.
   * @return Response.
   */
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

    db.user.storeProposalInDailyResult(u.id, qpr) ifSome { v =>
      OkApiResult(StoreProposalInDailyResultResult(v))
    }
  }

  /**
   * Stores result of voting of quest solution in db
   */
  def storeSolutionInDailyResult(request: StoreSolutionInDailyResultRequest): ApiResult[StoreSolutionInDailyResultResult] = handleDbException {
    import request._

    val u = ensurePrivateDailyResultExists(user)

    val qsr = QuestSolutionResult(
      solutionId = request.solution.id,
      battleId = request.battle.map(_.id),
      reward = request.reward,
      penalty = request.penalty,
      status = request.solution.status)

    db.user.storeSolutionInDailyResult(u.id, qsr) ifSome { v =>
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


