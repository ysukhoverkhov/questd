package controllers.domain.app.user

import models.domain._
import models.domain.view._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._
import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.quest._
import java.util.Date

case class GetQuestCostRequest(user: User)
case class GetQuestCostResult(allowed: ProfileModificationResult, cost: Assets)

case class PurchaseQuestRequest(user: User)
case class PurchaseQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class TakeQuestRequest(user: User)
case class TakeQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetTakeQuestCostRequest(user: User)
case class GetTakeQuestCostResult(allowed: ProfileModificationResult, cost: Assets)

case class ProposeSolutionRequest(user: User, solution: QuestSolutionInfoContent)
case class ProposeSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetQuestGiveUpCostRequest(user: User)
case class GetQuestGiveUpCostResult(allowed: ProfileModificationResult, cost: Assets)

case class GiveUpQuestRequest(user: User)
case class GiveUpQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class DeadlineQuestRequest(user: User)
case class DeadlineQuestResult(user: Option[User])

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

    val user = ensureNoDeadlineQuest(request.user)

    user.canPurchaseQuest match {
      case OK => {

        // Updating quest info.
        val v = if ((user.profile.questSolutionContext.purchasedQuest != None) && (user.stats.questsAcceptedPast > 0)) {
          val quest = db.quest.readById(user.profile.questSolutionContext.purchasedQuest.get.id)

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
              {
                val questCost = user.costOfPurchasingQuest
                val author = db.user.readById(q.authorUserId).map(x => PublicProfileWithID(q.authorUserId, x.profile.publicProfile))

                if (author == None) {
                  Logger.error("API - purchaseQuest. Unable to find quest author")
                  InternalErrorApiResult()
                } else {
                  adjustAssets(AdjustAssetsRequest(user = user, cost = Some(questCost))) map { r =>

                    val u = db.user.purchaseQuest(
                      r.user.id,
                      QuestInfoWithID(q.id, q.info),
                      author.get,
                      r.user.rewardForLosingQuest(q),
                      r.user.rewardForWinningQuest(q))

                    OkApiResult(Some(PurchaseQuestResult(OK, u.map(_.profile))))
                  }
                }
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
          val quest = db.quest.readById(request.user.profile.questSolutionContext.purchasedQuest.get.id)

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
          if (pq == None) {
            Logger.error("API - takeQuest. Purchased quest is None")
            InternalErrorApiResult()
          } else {
            val u = db.user.takeQuest(
              id = r.user.id,
              takenQuest = pq.get,
              cooldown = r.user.getCooldownForTakeQuest(pq.get.obj),
              deadline = r.user.getDeadlineForTakeQuest(pq.get.obj))
            OkApiResult(Some(TakeQuestResult(OK, u.map(_.profile))))
          }
        }
      }

      case (a: ProfileModificationResult) => OkApiResult(Some(TakeQuestResult(a)))
    }
  }

  /**
   * Propose solution for quest.
   */
  def proposeSolution(request: ProposeSolutionRequest): ApiResult[ProposeSolutionResult] = handleDbException {

    val user = ensureNoDeadlineQuest(request.user)

    user.canResolveQuest(ContentType.withName(request.solution.media.contentType)) match {
      case OK => {

        // TODO: pull user.profile.questSolutionContext.takenQuest.get to a val here. in 0.20.02
        db.solution.create(
          QuestSolution(
            userId = user.id,
            questLevel = user.profile.questSolutionContext.takenQuest.get.obj.level,
            info = QuestSolutionInfo(
              content = request.solution,
              themeId = user.profile.questSolutionContext.takenQuest.get.obj.themeId,
              questId = user.profile.questSolutionContext.takenQuest.get.id,
              vip = user.profile.publicProfile.vip),
            voteEndDate = user.solutionVoteEndDate(user.profile.questSolutionContext.takenQuest.get.obj)))

        val u = db.user.resetQuestSolution(
          user.id,
          config(api.ConfigParams.DebugDisableSolutionCooldown) == "1")

        OkApiResult(Some(ProposeSolutionResult(OK, u.map(_.profile))))
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
          val u = db.user.resetQuestSolution(
            r.user.id,
            config(api.ConfigParams.DebugDisableSolutionCooldown) == "1")
          OkApiResult(Some(GiveUpQuestResult(OK, u.map(_.profile))))
        }
      }

      case (a: ProfileModificationResult) => OkApiResult(Some(GiveUpQuestResult(a)))
    }
  }

  /**
   * Stop from solving quests because its deadline reached.
   */
  def deadlineQuest(request: DeadlineQuestRequest): ApiResult[DeadlineQuestResult] = handleDbException {
    storeSolutionOutOfTimePenalty(StoreSolutionOutOfTimePenaltyReqest(request.user, request.user.costOfGivingUpQuest)) map { r =>
      val u = db.user.resetQuestSolution(
        r.user.id,
        config(api.ConfigParams.DebugDisableSolutionCooldown) == "1")
      OkApiResult(Some(DeadlineQuestResult(u)))
    }
  }

  /**
   * Give quest solution author a reward on quest status change
   */
  def rewardQuestSolutionAuthor(request: RewardQuestSolutionAuthorRequest): ApiResult[RewardQuestSolutionAuthorResult] = handleDbException {
    import request._

    Logger.debug("API - rewardQuestSolutionAuthor")

    // TODO: read quest here only if this is required.
    // TODO: test banned users are penalized correctly.
    db.quest.readById(solution.info.questId) match {
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

          case QuestSolutionStatus.Lost =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution.id, reward = Some(author.profile.questSolutionContext.defeatReward)))

          case QuestSolutionStatus.CheatingBanned =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution.id, penalty = Some(author.penaltyForCheatingSolution(q))))

          case QuestSolutionStatus.IACBanned =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution.id, penalty = Some(author.penaltyForIACSolution(q))))
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
    // 1. find all solutions with the same quest id with status waiting for competitor.

    val solutionsForQuest = db.solution.allWithParams(
      status = Some(QuestSolutionStatus.WaitingForCompetitor.toString),
      questIds = List(request.solution.info.questId))

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

        if (other.userId != request.solution.userId) {

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

            val s = db.solution.updateStatus(curSol.id, QuestSolutionStatus.Won.toString, curSol.rivalSolutionId)

            val u = db.user.readById(curSol.userId)
            if (u != None) {
              rewardQuestSolutionAuthor(RewardQuestSolutionAuthorRequest(solution = s.get, author = u.get))
            }
          }

          // and losers
          for (curSol <- losers) {
            Logger.debug("  loser id=" + curSol.id)

            val s = db.solution.updateStatus(curSol.id, QuestSolutionStatus.Lost.toString, curSol.rivalSolutionId)

            val u = db.user.readById(curSol.userId)
            if (u != None) {
              rewardQuestSolutionAuthor(RewardQuestSolutionAuthorRequest(solution = s.get, author = u.get))
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

  private def ensureNoDeadlineQuest(user: User): User = {
    if (user.questDeadlineReached) {
      deadlineQuest(DeadlineQuestRequest(user)).body.get.user.get
    } else {
      user
    }
  }

}

