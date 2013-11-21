package controllers.domain.user

import models.domain._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._
import logic._
import protocol.ProfileModificationResult._

case class GetQuestCostRequest(user: User)
case class GetQuestCostResult(allowed: ProfileModificationResult, cost: Assets = Assets(0, 0, 0))

case class PurchaseQuestRequest(user: User)
case class PurchaseQuestResult(allowed: ProfileModificationResult, quest: Option[QuestInfo] = None)

case class TakeQuestRequest(user: User)
case class TakeQuestResult(allowed: ProfileModificationResult, theme: Option[QuestInfo] = None)

case class GetTakeQuestCostRequest(user: User)
case class GetTakeQuestCostResult(allowed: ProfileModificationResult, cost: Assets = Assets(0, 0, 0))

case class ProposeSolutionRequest(user: User, solution: QuestSolutionInfo)
case class ProposeSolutionResult(allowed: ProfileModificationResult)

case class GetQuestGiveUpCostRequest(user: User)
case class GetQuestGiveUpCostResult(allowed: ProfileModificationResult, cost: Assets = Assets(0, 0, 0))

case class GiveUpQuestRequest(user: User)
case class GiveUpQuestResult(allowed: ProfileModificationResult)

case class ResetPurchasesRequest(user: User)
case class ResetPurchasesResult()

private[domain] trait SolveQuestAPI { this: DBAccessor =>

  /**
   * Get cost of quest to shuffle.
   */
  def getQuestCost(request: GetQuestCostRequest): ApiResult[GetQuestCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetQuestCostResult(OK, user.costOfPurchasingQuest)))
  }

  /**
   * Purchase an option of quest to chose.
   */
  def purchaseQuest(request: PurchaseQuestRequest): ApiResult[PurchaseQuestResult] = handleDbException {
    import request._

    user.canPurchaseQuest match {
      case OK => {

        val q = user.getRandomQuestForSolution
        val questCost = user.costOfPurchasingQuest

        db.user.update {
          user.copy(
            profile = user.profile.copy(
              assets = user.profile.assets - questCost,
              questContext = user.profile.questContext.copy(
                numberOfPurchasedQuests = user.profile.questContext.numberOfPurchasedQuests + 1,
                purchasedQuest = Some(QuestInfoWithID(q.id, q.info)))))
        }

        OkApiResult(Some(PurchaseQuestResult(OK, Some(q.info))))
      }
      case a => OkApiResult(Some(PurchaseQuestResult(a)))
    }
  }

  /**
   * Get cost of taking quest to resolve.
   */
  def getTakeQuestCost(request: GetTakeQuestCostRequest): ApiResult[GetTakeQuestCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetTakeQuestCostResult(OK, user.costOfTakingQuest)))
  }

  /**
   * Take quest to deal with.
   */
  def takeQuest(request: TakeQuestRequest): ApiResult[TakeQuestResult] = handleDbException {
    import request._

    user.canTakeQuest match {

      case OK => {

        val pq = user.profile.questContext.purchasedQuest

        db.user.update {
          user.copy(
            profile = user.profile.copy(
              questContext = user.profile.questContext.copy(
                numberOfPurchasedQuests = 0,
                purchasedQuest = None,
                takenQuest = pq,
                questCooldown = user.getCooldownForTakeQuest(pq.get.obj)),
              assets = user.profile.assets - user.costOfTakingQuest))
        }

        OkApiResult(Some(TakeQuestResult(OK, Some(pq.get.obj))))
      }

      case (a: ProfileModificationResult) => OkApiResult(Some(TakeQuestResult(a)))
    }
  }

  /**
   * Propose solution for quest.
   */
  def proposeSolution(request: ProposeSolutionRequest): ApiResult[ProposeSolutionResult] = handleDbException {
    import request._

    user.canResulveQuest(ContentType.apply(solution.content.contentType)) match {
      case OK => {

        db.solution.create(
          QuestSolution(
            info = solution,
            userID = user.id,
            questID = user.profile.questContext.takenQuest.get.id))

        db.user.update {
          user.copy(
            profile = user.profile.copy(
              questContext = user.profile.questContext.copy(
                numberOfPurchasedQuests = 0,
                purchasedQuest = None,
                takenQuest = None)))
        }

        OkApiResult(Some(ProposeSolutionResult(OK)))
      }

      case (a: ProfileModificationResult) => OkApiResult(Some(ProposeSolutionResult(a)))
    }
  }

  /**
   * How much it'll take to give up quest.
   */
  def getQuestGiveUpCost(request: GetQuestGiveUpCostRequest): ApiResult[GetQuestGiveUpCostResult] = handleDbException {
    import request._

    OkApiResult(Some(GetQuestGiveUpCostResult(OK, user.costOfGivingUpQuest)))
  }

  /**
   * Give up quest and do not deal with it anymore.
   */
  def giveUpQuest(request: GiveUpQuestRequest): ApiResult[GiveUpQuestResult] = handleDbException {
    import request._

    user.canGiveUpQuest match {
      case OK => {
        val newAssets = (user.profile.assets - user.costOfGivingUpQuest).clamp

        db.user.update {
          user.copy(
            profile = user.profile.copy(
              questContext = user.profile.questContext.copy(
                numberOfPurchasedQuests = 0,
                purchasedQuest = None,
                takenQuest = None),
              assets = newAssets))
        }

        OkApiResult(Some(GiveUpQuestResult(OK)))
      }

      case (a: ProfileModificationResult) => OkApiResult(Some(GiveUpQuestResult(a)))
    }
  }

  /**
   * Reset all purchases (quests and themes) overnight.
   */
  def resetPurchases(request: ResetPurchasesRequest): ApiResult[ResetPurchasesResult] = handleDbException {
    import request._

    db.user.update {
      user.copy(
        profile = user.profile.copy(
          questContext = user.profile.questContext.copy(
            purchasedQuest = None,
            numberOfPurchasedQuests = 0),
          questProposalContext = user.profile.questProposalContext.copy(
            numberOfPurchasedThemes = 0)),
        schedules = user.schedules.copy(
          purchases = user.getResetPurchasesTimeout))
    }

    OkApiResult(Some(ResetPurchasesResult()))
  }
}



