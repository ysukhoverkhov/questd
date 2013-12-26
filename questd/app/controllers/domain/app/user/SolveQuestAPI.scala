package controllers.domain.app.user

import models.domain._
import models.domain.base._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._
import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.quest._

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

case class RewardQuestSolutionAuthorRequest(solution: QuestSolution, author: User)
case class RewardQuestSolutionAuthorResult()

case class TryFightQuestRequest(solution: QuestSolution)
case class TryFightQuestResult()

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

    val user = if (request.user.shouldGiveupQuest) {
      giveUpQuest(GiveUpQuestRequest(request.user.user))
      db.user.readByID(request.user.id).get
    } else {
      request.user
    }
    
    user.canPurchaseQuest match {
      case OK => {

        // Updating quest info.
        val v = if ((user.profile.questSolutionContext.purchasedQuest != None) && (user.stats.questsAcceptedPast > 0)) {
          val quest = db.quest.readByID(user.profile.questSolutionContext.purchasedQuest.get.id)

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
          user.getRandomQuestForSolution match {
            case None => OkApiResult(Some(PurchaseQuestResult(OutOfContent)))
            case Some(q) => {
              val questCost = user.costOfPurchasingQuest
              val author = db.user.readByID(q.authorUserID).map(x => BioWithID(q.authorUserID, x.profile.bio))

              adjustAssets(AdjustAssetsRequest(user = user, cost = Some(questCost))) map { r =>

                val u = r.user.copy(
                  profile = r.user.profile.copy(
                    questSolutionContext = r.user.profile.questSolutionContext.copy(
                      numberOfPurchasedQuests = r.user.profile.questSolutionContext.numberOfPurchasedQuests + 1,
                      purchasedQuest = Some(QuestInfoWithID(q.id, q.info)),
                      questAuthor = author,
                      defeatReward = r.user.rewardForLosingQuest(q),
                      victoryReward = r.user.rewardForWinningQuest(q))),
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
                questCooldown = r.user.getCooldownForTakeQuest(pq.get.obj),
                questDeadline = r.user.getDeadlineForTakeQuest(pq.get.obj))),
                
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

    val user = if (request.user.shouldGiveupQuest) {
      giveUpQuest(GiveUpQuestRequest(request.user.user))
      db.user.readByID(request.user.id).get
    } else {
      request.user
    }

    user.canResulveQuest(ContentType.withName(request.solution.content.contentType)) match {
      case OK => {

        db.solution.create(
          QuestSolution(
            info = request.solution,
            userID = user.id,
            questID = user.profile.questSolutionContext.takenQuest.get.id,
            questLevel = user.profile.questSolutionContext.takenQuest.get.obj.level))

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

  /**
   * Give quest solution author a reward on quest status change
   */
  def rewardQuestSolutionAuthor(request: RewardQuestSolutionAuthorRequest): ApiResult[RewardQuestSolutionAuthorResult] = handleDbException {
    import request._

    Logger.debug("API - rewardQuestSolutionAuthor")

    db.quest.readByID(solution.questID) match {
      case Some(q) => {
        
        val r = QuestSolutionStatus.withName(solution.status) match {
          case QuestSolutionStatus.OnVoting => {
            Logger.error("We are rewarding player for solution what is on voting.")
            InternalErrorApiResult()
          }

          case QuestSolutionStatus.WaitingForCompetitor =>
            tryFightQuest(TryFightQuestRequest(solution)) map OkApiResult(Some(StoreSolutionInDailyResultResult(author)))

          case QuestSolutionStatus.Won =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution.id, reward = Some(author.profile.questSolutionContext.victoryReward)))
            //adjustAssets(AdjustAssetsRequest(user = author, reward = Some(author.profile.questSolutionContext.victoryReward)))

          case QuestSolutionStatus.Lost =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution.id, reward = Some(author.profile.questSolutionContext.defeatReward)))
            //adjustAssets(AdjustAssetsRequest(user = author, reward = Some(author.profile.questSolutionContext.defeatReward)))

          case QuestSolutionStatus.CheatingBanned =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution.id, penalty = Some(author.penaltyForCheatingSolution(q))))
//            adjustAssets(AdjustAssetsRequest(user = author, cost = Some(author.penaltyForCheatingSolution(q))))

          case QuestSolutionStatus.IACBanned =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution.id, penalty = Some(author.penaltyForIACSolution(q))))
//            adjustAssets(AdjustAssetsRequest(user = author, cost = Some(author.penaltyForIACSolution(q))))
        }

        r map {
          OkApiResult(Some(RewardQuestSolutionAuthorResult()))
        }
      }
      case None => {
        Logger.error("No quest found for updating player assets for changing solution state.")
        InternalErrorApiResult()
      }
    }
  }

  /**
   * Tries to find competitor to us on quest and resolve our battle. Updates db after that.
   */
  def tryFightQuest(request: TryFightQuestRequest): ApiResult[TryFightQuestResult] = handleDbException {
    // 1. find all solutions with the same quest id with status waiting for compettor.

    Logger.error("!!!! Very slow request here. Replace with findOne and order by date excluding self.") // Also use here findandmodify with updating last access timestampt.
    // EVERYWHERE there we update last access timestamp we should use findandmodify to make the action atomic.
    val solutionsForQuest = db.solution.allWithStatusAndQuest(QuestSolutionStatus.WaitingForCompetitor.toString, request.solution.questID)

    def fight(s1: QuestSolution, s2: QuestSolution): (List[QuestSolution], List[QuestSolution]) = {
      if (s1.calculatePoints == s2.calculatePoints)
        (List(s1, s2), List())
      else if (s1.calculatePoints > s2.calculatePoints)
        (List(s1), List(s2))
      else
        (List(s2), List(s1))
    }

    def compete(solutions: Iterator[QuestSolution]): ApiResult[TryFightQuestResult] = {
      if (solutions.hasNext) {
        val other = solutions.next

        if (other.userID != request.solution.userID) {

          Logger.debug("Found fight pair for quest " + request.solution + ":")
          Logger.debug("  s1.id=" + request.solution.id)
          Logger.debug("  s2.id=" + other.id)
          
          // Updating solution rivals
          val ourSol = request.solution.copy(rivalSolutionId = Some(other.id))
          val otherSol = other.copy(rivalSolutionId = Some(request.solution.id))
          
          // Compare two solutions.
          val (winners, losers) = fight(otherSol, ourSol)

          // update solutions, winners 
          for (curSol <- winners) {
            Logger.debug("  winner id=" + curSol.id)

            val s = curSol.copy(
                status = QuestSolutionStatus.Won.toString) 
            db.solution.update(s)
              
            val u = db.user.readByID(curSol.userID)
            if (u != None) {
              rewardQuestSolutionAuthor(RewardQuestSolutionAuthorRequest(solution = s, author = u.get))
//              adjustAssets(AdjustAssetsRequest(user = u.get, reward = Some(u.get.profile.questSolutionContext.victoryReward)))
            }
          }

          // and losers
          for (curSol <- losers) {
            Logger.debug("  loser id=" + curSol.id)

            val s = curSol.copy(
                status = QuestSolutionStatus.Lost.toString) 
            db.solution.update(s)

            val u = db.user.readByID(curSol.userID)
            if (u != None) {
              rewardQuestSolutionAuthor(RewardQuestSolutionAuthorRequest(solution = s, author = u.get))
              //adjustAssets(AdjustAssetsRequest(user = u.get, reward = Some(u.get.profile.questSolutionContext.defeatReward)))
            }
          }
          
          OkApiResult(Some(TryFightQuestResult()))

        } else {

          // Skipping to next if current is we are.
          compete(solutions)
        }
      } else {

        // We didn;t find competitor but this is ok.
        OkApiResult(Some(TryFightQuestResult()))
      }
    }

    compete(solutionsForQuest)
  }

}


