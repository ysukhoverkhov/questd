package controllers.domain.app.user

import scala.annotation.tailrec
import scala.language.postfixOps
import models.domain._
import play.Logger
import controllers.domain.helpers._
import controllers.domain._
import components._
import controllers.domain.app.protocol.ProfileModificationResult._

case class GetQuestCostRequest(user: User)
case class GetQuestCostResult(allowed: ProfileModificationResult, cost: Option[Assets] = None)

case class PurchaseQuestRequest(user: User)
case class PurchaseQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class TakeQuestRequest(user: User)
case class TakeQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class AddToMustVoteSolutionsRequest(user: User, friendIds: List[String], solutionId: String)
case class AddToMustVoteSolutionsResult(user: User)

case class ProposeSolutionRequest(
  user: User,
  questId: String,
  solution: QuestSolutionInfoContent/*, friendsToHelp: List[String] = List()*/)
case class ProposeSolutionResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetQuestGiveUpCostRequest(user: User)
case class GetQuestGiveUpCostResult(allowed: ProfileModificationResult, cost: Option[Assets] = None)

case class GiveUpQuestRequest(user: User)
case class GiveUpQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetQuestSolutionHelpCostRequest(user: User)
case class GetQuestSolutionHelpCostResult(allowed: ProfileModificationResult, cost: Option[Assets] = None)

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
//  def getQuestCost(request: GetQuestCostRequest): ApiResult[GetQuestCostResult] = handleDbException {
//    import request._
//
//    OkApiResult(GetQuestCostResult(OK, Some(user.costOfPurchasingQuest)))
//  }

  /**
   * Purchase an option of quest to chose.
   */
  // TODO: clean me up.
  def purchaseQuest(request: PurchaseQuestRequest): ApiResult[PurchaseQuestResult] = handleDbException {

//    val user = ensureNoDeadlineQuest(request.user)
//
//    user.canPurchaseQuest match {
//      case OK =>
//
//        // Updating quest info.
//        val v = if (user.stats.questsAcceptedPast > 0) {
//
//          user.profile.questSolutionContext.purchasedQuest ifSome { q =>
//            db.quest.readById(q.id) ifSome { q =>
//              skipQuest(SkipQuestRequest(q))
//            }
//          }
//        } else {
//          OkApiResult(SkipQuestResult())
//        }
//
//        v ifOk {
//
//          // Updating user profile.
//          user.getRandomQuestForSolution match {
//            case None => OkApiResult(PurchaseQuestResult(OutOfContent))
//
//            case Some(q) =>
//              val questCost = user.costOfPurchasingQuest
//              db.user.readById(q.info.authorId).map(x => PublicProfileWithID(q.info.authorId, x.profile.publicProfile)) ifSome { author =>
//                adjustAssets(AdjustAssetsRequest(user = user, cost = Some(questCost))) ifOk { r =>
//
//                  val u = db.user.purchaseQuest(
//                    r.user.id,
//                    QuestInfoWithID(q.id, q.info),
//                    author,
//                    r.user.rewardForLosingQuest(q),
//                    r.user.rewardForWinningQuest(q))
//
//                  OkApiResult(PurchaseQuestResult(OK, u.map(_.profile)))
//                }
//              }
//          }
//        }
//
//      case a => OkApiResult(PurchaseQuestResult(a))
//    }
    OkApiResult(PurchaseQuestResult(OK))
  }

  /**
   * Take quest to deal with.
   */
//  def takeQuest(request: TakeQuestRequest): ApiResult[TakeQuestResult] = handleDbException {
//    request.user.canTakeQuest match {
//
//      case OK =>
//
//        // Updating quest info.
//        val v = if (request.user.stats.questsAcceptedPast > 0) {
//
//          request.user.profile.questSolutionContext.purchasedQuest ifSome { q =>
//            db.quest.readById(q.id) ifSome { q =>
//              val ratio = Math.round(request.user.stats.questsReviewedPast.toFloat / request.user.stats.questsAcceptedPast) - 1
//              takeQuestUpdate(TakeQuestUpdateRequest(q, ratio))
//            }
//          }
//
//        } else {
//          OkApiResult(TakeQuestUpdateResult)
//        }
//
//        v ifOk {
//          adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(request.user.costOfTakingQuest)))
//        } ifOk { r =>
//
//          // Updating user profile.
//          r.user.profile.questSolutionContext.purchasedQuest ifSome { pq =>
//
//            db.user.takeQuest(
//              id = r.user.id,
//              pq,
//              r.user.getCooldownForTakeQuest(pq.obj),
//              r.user.getDeadlineForTakeQuest(pq.obj)) ifSome { usr =>
//
//                OkApiResult(TakeQuestResult(OK, Some(usr.profile)))
//
//              }
//          }
//        }
//
//      case (a: ProfileModificationResult) => OkApiResult(TakeQuestResult(a))
//    }
//  }

  /**
   * Add a quest to given friends "mustVote" list
   */
//  def addToMustVoteSolutions(request: AddToMustVoteSolutionsRequest): ApiResult[AddToMustVoteSolutionsResult] = handleDbException {
//    val filteredFriends = request.friendIds.filter( request.user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId).contains(_) )
//
//    if (request.friendIds.isEmpty) {
//      OkApiResult(AddToMustVoteSolutionsResult(request.user))
//    } else {
//      db.user.populateMustVoteSolutionsList(
//        userIds = filteredFriends,
//        solutionId = request.solutionId)
//
//      {
//        adjustAssets(AdjustAssetsRequest(
//          user = request.user,
//          cost = Some(request.user.costOfAskingForHelpWithSolution * request.friendIds.length)))
//      } ifOk { r =>
//        OkApiResult(AddToMustVoteSolutionsResult(r.user))
//      }
//    }
//  }

  /**
   * Propose solution for quest.
   */
  // TODO: rename to SolveQuest
  def proposeSolution(request: ProposeSolutionRequest): ApiResult[ProposeSolutionResult] = handleDbException {
// TODO: implement me.
//    val user = ensureNoDeadlineQuest(request.user)

    // TODO: check cost of solving quest here and amount of our money.
    request.user.canResolveQuest(
      contentType = request.solution.media.contentType,
      questId = request.questId
      /*,
      friendsInvited = request.friendsToHelp.length*/) match {
      case OK =>

        def content = if (request.user.payedAuthor) {
          request.solution
        } else {
          request.solution
        }

        {
          makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.SubmitQuestResult)))
        } ifOk { r =>

          db.quest.readById(request.questId) ifSome { takenQuest =>
            r.user.demo.cultureId ifSome { culture =>

              val solution = QuestSolution(
                cultureId = culture,
                questLevel = takenQuest.info.level,
                info = QuestSolutionInfo(
                  content = content,
                  authorId = r.user.id,
                  questId = takenQuest.id,
                  vip = request.user.profile.publicProfile.vip),
                voteEndDate = request.user.solutionVoteEndDate(takenQuest.info))

              db.solution.create(solution)

              // TODO: remove "reset" completelly. or make it empty.
              db.user.resetQuestSolution(
                request.user.id,
                config(api.ConfigParams.DebugDisableSolutionCooldown) == "1") ifSome { u =>

                {
                  addToTimeLine(AddToTimeLineRequest(
                    user = u,
                    reason = TimeLineReason.Created,
                    objectType = TimeLineType.Solution,
                    objectId = solution.id))
                } ifOk { r =>
                  addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                    user = u,
                    reason = TimeLineReason.Created,
                    objectType = TimeLineType.Solution,
                    objectId = solution.id))
//                } ifOk { r =>
//                  addToMustVoteSolutions(AddToMustVoteSolutionsRequest(u, request.friendsToHelp, solution.id))
                } ifOk { r =>
                  OkApiResult(ProposeSolutionResult(OK, Some(r.user.profile)))
                }
              }
            }
          }
        }

      case (a: ProfileModificationResult) => OkApiResult(ProposeSolutionResult(a))
    }
  }

  /**
   * How much it'll take to give up quest.
   */
//  def getQuestGiveUpCost(request: GetQuestGiveUpCostRequest): ApiResult[GetQuestGiveUpCostResult] = handleDbException {
//    import request._
//
//    OkApiResult(GetQuestGiveUpCostResult(OK, Some(user.costOfGivingUpQuest)))
//  }

  /**
   * Give up quest and do not deal with it anymore.
   */
//  def giveUpQuest(request: GiveUpQuestRequest): ApiResult[GiveUpQuestResult] = handleDbException {
//    request.user.canGiveUpQuest match {
//      case OK =>
//
//        adjustAssets(AdjustAssetsRequest(user = request.user, cost = Some(request.user.costOfGivingUpQuest))) ifOk { r =>
//          val u = db.user.resetQuestSolution(
//            r.user.id,
//            config(api.ConfigParams.DebugDisableSolutionCooldown) == "1")
//          OkApiResult(GiveUpQuestResult(OK, u.map(_.profile)))
//        }
//
//      case (a: ProfileModificationResult) => OkApiResult(GiveUpQuestResult(a))
//    }
//  }

//  def getQuestSolutionHelpCost(request: GetQuestSolutionHelpCostRequest): ApiResult[GetQuestSolutionHelpCostResult] = handleDbException {
//    import request._
//
//    OkApiResult(GetQuestSolutionHelpCostResult(OK, Some(user.costOfAskingForHelpWithSolution)))
//  }

  /**
   * Stop from solving quests because its deadline reached.
   */
//  def deadlineQuest(request: DeadlineQuestRequest): ApiResult[DeadlineQuestResult] = handleDbException {
//    storeSolutionOutOfTimePenalty(StoreSolutionOutOfTimePenaltyReqest(request.user, request.user.costOfGivingUpQuest)) ifOk { r =>
//      val u = db.user.resetQuestSolution(
//        r.user.id,
//        config(api.ConfigParams.DebugDisableSolutionCooldown) == "1")
//      OkApiResult(DeadlineQuestResult(u))
//    }
//  }

  /**
   * Give quest solution author a reward on quest status change
   */
  def rewardQuestSolutionAuthor(request: RewardQuestSolutionAuthorRequest): ApiResult[RewardQuestSolutionAuthorResult] = handleDbException {
    // TODO: implement me.
//    import request._
//
//    Logger.debug("API - rewardQuestSolutionAuthor")
//
//    case class QuestNotFoundException() extends Throwable
//
//    def q = {
//      db.quest.readById(solution.info.questId) match {
//        case Some(qu) => qu
//        case None => throw QuestNotFoundException()
//      }
//    }
//
//    try {
//      val r = solution.status match {
//        case QuestSolutionStatus.OnVoting =>
//          Logger.error("We are rewarding player for solution what is on voting.")
//          InternalErrorApiResult()
//
//        case QuestSolutionStatus.WaitingForCompetitor =>
//          tryFightQuest(TryFightQuestRequest(solution)) ifOk OkApiResult(StoreSolutionInDailyResultResult(author))
//
//        case QuestSolutionStatus.Won =>
//            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution, reward = Some(author.profile.questSolutionContext.victoryReward)))
//
//        case QuestSolutionStatus.Lost =>
//            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution, reward = Some(author.profile.questSolutionContext.defeatReward)))
//
//        case QuestSolutionStatus.CheatingBanned =>
//            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution, penalty = Some(author.penaltyForCheatingSolution(q))))
//
//        case QuestSolutionStatus.IACBanned =>
//            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution, penalty = Some(author.penaltyForIACSolution(q))))
//      }
//
//      r ifOk {
//        OkApiResult(RewardQuestSolutionAuthorResult())
//      }
//    } catch {
//      case ex: QuestNotFoundException =>
//        Logger.error("No quest found for updating player assets for changing solution state.")
//        InternalErrorApiResult()
//    }
        OkApiResult(RewardQuestSolutionAuthorResult())
  }

  /**
   * Tries to find competitor to us on quest and resolve our battle. Updates db after that.
   */
  def tryFightQuest(request: TryFightQuestRequest): ApiResult[TryFightQuestResult] = handleDbException {
    // 1. find all solutions with the same quest id with status waiting for competitor.

    val solutionsForQuest = db.solution.allWithParams(
      status = List(QuestSolutionStatus.WaitingForCompetitor.toString),
      questIds = List(request.solution.info.questId))

    def fight(s1: QuestSolution, s2: QuestSolution): (List[QuestSolution], List[QuestSolution]) = {
      if (s1.calculatePoints == s2.calculatePoints)
        (List(s1, s2), List())
      else if (s1.calculatePoints > s2.calculatePoints)
        (List(s1), List(s2))
      else
        (List(s2), List(s1))
    }

    @tailrec
    def compete(solutions: Iterator[QuestSolution]): ApiResult[TryFightQuestResult] = {
      if (solutions.hasNext) {
        val other = solutions.next()

        if (other.info.authorId != request.solution.info.authorId) {

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

            db.solution.updateStatus(curSol.id, QuestSolutionStatus.Won.toString, curSol.rivalSolutionId) ifSome { s =>
              db.user.readById(curSol.info.authorId) ifSome { u =>
                rewardQuestSolutionAuthor(RewardQuestSolutionAuthorRequest(solution = s, author = u))
              }
            }

          }

          // and losers
          for (curSol <- losers) {
            Logger.debug("  loser id=" + curSol.id)

            db.solution.updateStatus(curSol.id, QuestSolutionStatus.Lost.toString, curSol.rivalSolutionId) ifSome { s =>
              db.user.readById(curSol.info authorId) ifSome { u =>
                rewardQuestSolutionAuthor(RewardQuestSolutionAuthorRequest(solution = s, author = u))
              }
            }

          }

          OkApiResult(TryFightQuestResult())

        } else {

          // Skipping to next if current is we are.
          compete(solutions)
        }
      } else {

        // We didn;t find competitor but this is ok.
        OkApiResult(TryFightQuestResult())
      }
    }

    compete(solutionsForQuest)
  }

  // it should return not user but its option.
//  private def ensureNoDeadlineQuest(user: User): User = {
//    if (user.questDeadlineReached) {
//      deadlineQuest(DeadlineQuestRequest(user)).body.get.user.get
//    } else {
//      user
//    }
//  }

}

