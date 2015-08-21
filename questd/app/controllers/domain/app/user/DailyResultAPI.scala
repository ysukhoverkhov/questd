package controllers.domain.app.user

import controllers.domain.app.quest.GetMyQuestsRequest
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import models.domain.battle.Battle
import models.domain.common.Assets
import models.domain.quest.{QuestStatus, Quest}
import models.domain.solution.Solution
import models.domain.user._
import models.domain.user.dailyresults._
import models.domain.user.profile.Profile
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

case class StoreQuestInDailyResultRequest(
  user: User,
  quest: Quest,
  reward: Assets)
case class StoreQuestInDailyResultResult(user: User)

case class StoreBattleInDailyResultRequest(
  user: User,
  battle: Battle,
  reward: Assets)
case class StoreBattleInDailyResultResult(user: User)

case class StoreSolutionInDailyResultRequest(
  user: User,
  solution: Solution,
  reward: Assets)
case class StoreSolutionInDailyResultResult(user: User)


private[domain] trait DailyResultAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Shifts daily result.
   */ // TODO: bug here. daily result is added for each created quest, should create only one private daily result.
  // TODO: bug here - this function should work if no daily results present.
  // TODO: write test to confirm this bug and fix it after that (there is a chance I'm wrong and there is on such bug).
  def shiftDailyResult(request: ShiftDailyResultRequest): ApiResult[ShiftDailyResultResult] = handleDbException {
    import request._

    {
      getMyQuests(
        GetMyQuestsRequest(
          user = user,
          status = QuestStatus.InRotation
        ))
    } map { r =>
      r.quests.foldLeft[ApiResult[AddQuestIncomeToDailyResultResult]](OkApiResult(AddQuestIncomeToDailyResultResult(user))) {
        case (OkApiResult(_), q) =>
          addQuestIncomeToDailyResult(AddQuestIncomeToDailyResultRequest(user, q))
        case (badResult, _) =>
          badResult
      }
    } map { r =>
      db.user.addPrivateDailyResult(
        r.user.id,
        DailyResult(
          user.getStartOfCurrentDailyResultPeriod)) ifSome { u =>

        OkApiResult(ShiftDailyResultResult(u))
      }
    }
  }

  /**
   * Returns moves ready daily results to public daily results and returns public results to client
   */
  def getDailyResult(request: GetDailyResultRequest): ApiResult[GetDailyResultResult] = handleDbException {

    def applyDailyResults(u: User) = {

      val deltaAssets = u.profile.dailyResults.foldLeft(Assets()) { (a, dailyResult) =>

        a + List[Assets](
          dailyResult.decidedQuests.foldLeft(Assets()) { (a, result) =>
            a + result.reward
          },
          dailyResult.decidedSolutions.foldLeft(Assets()) { (a, result) =>
            a + result.reward
          },
          dailyResult.decidedBattles.foldLeft(Assets()) { (a, result) =>
            a + result.reward
          },
          dailyResult.questsIncome.foldLeft(Assets()) { (a, questIncome) =>
            a + questIncome.likesIncome + questIncome.passiveIncome + questIncome.solutionsIncome
          }
        ).foldLeft(Assets()) { (r, assets) =>
          r + assets
        }
      }

      adjustAssets(AdjustAssetsRequest(user = u, change = deltaAssets))
    }

    // Check replace old public daily results with new daily results.
    val (u, newOne, internalError) = if (request.user.privateDailyResults.length > 1) {
      db.user.movePrivateDailyResultsToPublic(request.user.id, request.user.privateDailyResults.tail) match {
        case Some(us) =>
          if (us.privateDailyResults.length < 1) {
            Logger.error(s"Zero private daily results for user ${us.id}")
            Logger.error(s"Was moving ${request.user.privateDailyResults.tail}")
            Logger.error(s"Was before moving ${request.user.privateDailyResults}")
          }
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

    user.privateDailyResults.head.questsIncome.find(_.questId == quest.id) match {

      case Some(dailyResultEntry) =>
        val reward = if (dailyResultEntry.timesSolved < MaxRewardedQuestSolutionsPerDay)
          quest.rewardForSolution
        else
          Assets()

        db.user.storeQuestSolvingInDailyResult(
          user.id,
          quest.id,
          reward
        ) ifSome { updatedUser =>
          OkApiResult(StoreQuestSolvingInDailyResultResult(updatedUser))
        }

      case None =>
        OkApiResult(StoreQuestSolvingInDailyResultResult(user))
    }
  }

  /**
   * Adds questIncome to current private daily result.
   * @param request Request containing required information.
   * @return The response.
   */
  def addQuestIncomeToDailyResult(request: AddQuestIncomeToDailyResultRequest): ApiResult[AddQuestIncomeToDailyResultResult] = handleDbException {
    import request._

    if (user.privateDailyResults.head.questsIncome.exists(_.questId == quest.id)) {
      OkApiResult(AddQuestIncomeToDailyResultResult(user))
    } else {
      db.user.addQuestIncomeToDailyResult(user.id, createQuestIncomeForQuest(quest)) ifSome { u =>
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
  def storeQuestInDailyResult(request: StoreQuestInDailyResultRequest): ApiResult[StoreQuestInDailyResultResult] = handleDbException {
    import request._

    val qpr = QuestResult(
      questId = request.quest.id,
      reward = request.reward,
      status = request.quest.status)

    db.user.storeQuestInDailyResult(user.id, qpr) ifSome { v =>
      OkApiResult(StoreQuestInDailyResultResult(v))
    }
  }

  /**
   * Stores result of voting of quest solution in db
   */
  def storeSolutionInDailyResult(request: StoreSolutionInDailyResultRequest): ApiResult[StoreSolutionInDailyResultResult] = handleDbException {
    import request._

    val qsr = SolutionResult(
      solutionId = request.solution.id,
      reward = request.reward,
      status = request.solution.status)

    db.user.storeSolutionInDailyResult(user.id, qsr) ifSome { v =>
      OkApiResult(StoreSolutionInDailyResultResult(v))
    }
  }

  /**
   * Stores battle in daily result.
   */
  def storeBattleInDailyResult(request: StoreBattleInDailyResultRequest): ApiResult[StoreBattleInDailyResultResult] = handleDbException {
    import request._

    val battleResult = BattleResult(
      battleId = battle.id,
      reward = reward,
      isVictory = battle.info.battleSides.find(_.authorId == user.id).fold(false)(_.isWinner))

    db.user.storeBattleInDailyResult(user.id, battleResult) ifSome { u =>
      OkApiResult(StoreBattleInDailyResultResult(u))
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
}

