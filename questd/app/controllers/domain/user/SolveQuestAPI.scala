package controllers.domain.user

import models.domain._
import models.domain.base._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._
import logic._
import protocol.ProfileModificationResult._

case class GetQuestCostRequest(user: User)
case class GetQuestCostResult(allowed: ProfileModificationResult, cost: Assets)

case class PurchaseQuestRequest(user: User)
case class PurchaseQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class TakeQuestRequest(user: User)
case class TakeQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetTakeQuestCostRequest(user: User)
case class GetTakeQuestCostResult(allowed: ProfileModificationResult, cost: Assets)

case class ProposeSolutionRequest(user: User, solution: QuestSolutionInfo)
case class ProposeSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetQuestGiveUpCostRequest(user: User)
case class GetQuestGiveUpCostResult(allowed: ProfileModificationResult, cost: Assets)

case class GiveUpQuestRequest(user: User)
case class GiveUpQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

private[domain] trait SolveQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

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
    request.user.canPurchaseQuest match {
      case OK => {

        // Updating quest info.
        val v = if ((request.user.profile.questSolutionContext.purchasedQuest != None) && (request.user.stats.questsAcceptedPast > 0)) {
          val quest = db.quest.readByID(request.user.profile.questSolutionContext.purchasedQuest.get.id)

          quest match {
            case None => {
              Logger.error("Quest by id not found in purchaseQuest")
              InternalErrorApiResult()
            }

            case Some(q) => skipQuest(SkipQuestRequest(q))
          }
        } else {
          OkApiResult(Some(SkipQuestResult()))
        }

        v map {

          // Updating user profile.
          request.user.getRandomQuestForSolution match {
            case None => OkApiResult(Some(PurchaseQuestResult(OutOfContent)))
            case Some(q) => {
              val questCost = request.user.costOfPurchasingQuest

              adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(questCost))) map { r =>

                val u = r.user.copy(
                  profile = r.user.profile.copy(
                    questSolutionContext = r.user.profile.questSolutionContext.copy(
                      numberOfPurchasedQuests = r.user.profile.questSolutionContext.numberOfPurchasedQuests + 1,
                      purchasedQuest = Some(QuestInfoWithID(q.id, q.info)))),
                  stats = r.user.stats.copy(
                    questsReviewed = r.user.stats.questsReviewed + 1))
                db.user.update(u)

                OkApiResult(Some(PurchaseQuestResult(OK, Some(u.profile))))
              }
            }
          }
        }
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
    request.user.canTakeQuest match {

      case OK => {

        // Updating quest info.
        val v = if (request.user.stats.questsAcceptedPast > 0) {
          val quest = db.quest.readByID(request.user.profile.questSolutionContext.purchasedQuest.get.id)

          quest match {
            case None => {
              Logger.error("Quest by id not found n purchaseQuest")
              InternalErrorApiResult()
            }

            case Some(q) => {
              val ratio = Math.round(request.user.stats.questsReviewedPast.toFloat / request.user.stats.questsAcceptedPast) - 1

              takeQuestUpdate(TakeQuestUpdateRequest(q, ratio))
            }
          }
        } else {
          OkApiResult(Some(TakeQuestUpdateResult))
        }

        v map {
          adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(request.user.costOfTakingQuest)))
        } map { r =>

          // Updating user profile.
          val pq = r.user.profile.questSolutionContext.purchasedQuest

          val u = r.user.copy(
            profile = r.user.profile.copy(
              questSolutionContext = r.user.profile.questSolutionContext.copy(
                numberOfPurchasedQuests = 0,
                purchasedQuest = None,
                takenQuest = pq,
                questCooldown = r.user.getCooldownForTakeQuest(pq.get.obj))),
            stats = r.user.stats.copy(
              questsAccepted = r.user.stats.questsAccepted + 1))
          db.user.update(u)

          OkApiResult(Some(TakeQuestResult(OK, Some(u.profile))))
        }

      }

      case (a: ProfileModificationResult) => OkApiResult(Some(TakeQuestResult(a)))
    }
  }

  /**
   * Propose solution for quest.
   */
  def proposeSolution(request: ProposeSolutionRequest): ApiResult[ProposeSolutionResult] = handleDbException {
    import request._

    user.canResulveQuest(ContentType.withName(solution.content.contentType)) match {
      case OK => {

        db.solution.create(
          QuestSolution(
            info = solution,
            userID = user.id,
            questID = user.profile.questSolutionContext.takenQuest.get.id,
            questlevel = user.profile.questSolutionContext.takenQuest.get.obj.level))

        val u = user.copy(
          profile = user.profile.copy(
            questSolutionContext = user.profile.questSolutionContext.copy(
              numberOfPurchasedQuests = 0,
              purchasedQuest = None,
              takenQuest = None)))

        db.user.update(u)

        OkApiResult(Some(ProposeSolutionResult(OK, Some(u.profile))))
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
    request.user.canGiveUpQuest match {
      case OK => {

        adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(request.user.costOfGivingUpQuest))) map { r =>
          val u = r.user.copy(
            profile = r.user.profile.copy(
              questSolutionContext = r.user.profile.questSolutionContext.copy(
                numberOfPurchasedQuests = 0,
                purchasedQuest = None,
                takenQuest = None)))
          db.user.update(u)

          OkApiResult(Some(GiveUpQuestResult(OK, Some(u.profile))))
        }
      }

      case (a: ProfileModificationResult) => OkApiResult(Some(GiveUpQuestResult(a)))
    }
  }

}



