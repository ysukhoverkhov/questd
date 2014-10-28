package controllers.domain.app.user

import controllers.domain.app.quest.SolveQuestUpdateRequest

import scala.annotation.tailrec
import scala.language.postfixOps
import models.domain._
import play.Logger
import controllers.domain.helpers._
import controllers.domain._
import components._
import controllers.domain.app.protocol.ProfileModificationResult._

case class AddToMustVoteSolutionsRequest(user: User, friendIds: List[String], solutionId: String)
case class AddToMustVoteSolutionsResult(user: User)

case class SolveQuestRequest(
  user: User,
  questId: String,
  solution: QuestSolutionInfoContent)
case class SolveQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetQuestSolutionHelpCostRequest(user: User)
case class GetQuestSolutionHelpCostResult(allowed: ProfileModificationResult, cost: Option[Assets] = None)

case class RewardQuestSolutionAuthorRequest(solution: QuestSolution, author: User)
case class RewardQuestSolutionAuthorResult()

case class TryFightQuestRequest(solution: QuestSolution)
case class TryFightQuestResult()

private[domain] trait SolveQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>


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
  def solveQuest(request: SolveQuestRequest): ApiResult[SolveQuestResult] = handleDbException {
    import request._

    // TODO: tests:
    // 1. canSolveQuest
    // 2. updating quest points with correct amount.


    db.quest.readById(questId) match {
      case None => OkApiResult(SolveQuestResult(OutOfContent))
      case Some(questToSolve) =>
        user.canSolveQuest(contentType = solution.media.contentType, questToSolve = questToSolve) match {
          case OK =>

            def content = if (user.payedAuthor) {
              solution
            } else {
              solution
            }

            {
              makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.SubmitQuestResult)))
            } ifOk { r =>

              r.user.demo.cultureId ifSome { culture =>

                val solution = QuestSolution(
                  cultureId = culture,
                  questLevel = questToSolve.info.level,
                  info = QuestSolutionInfo(
                    content = content,
                    authorId = r.user.id,
                    questId = questToSolve.id,
                    vip = user.profile.publicProfile.vip),
                  voteEndDate = user.solutionVoteEndDate(questToSolve.info))

                // Creating solution.
                db.solution.create(solution)

                val numberOfReviewedQuests = user.timeLine.count(_.objectType == TimeLineType.Quest)
                val numberOfSolvedQuests = user.timeLine.count(te => (te.objectType == TimeLineType.Solution)
                  && (te.reason == TimeLineReason.Created)
                  && (te.entryAuthorId == user.id))

                // Updating quest points.
                val ratio = if (numberOfSolvedQuests == 0)
                  1
                else
                  Math.round(numberOfReviewedQuests / numberOfSolvedQuests) - 1
                solveQuestUpdate(SolveQuestUpdateRequest(questToSolve, ratio))

                {
                  if (user.profile.questSolutionContext.bookmarkedQuest.map(_.id) == Some(questToSolve.id))
                    db.user.resetQuestBookmark(user.id)
                  else
                    Some(user)
                } ifSome { u =>
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
                    OkApiResult(SolveQuestResult(OK, Some(r.user.profile)))
                  }
                }
              }
            }

          case (a: ProfileModificationResult) => OkApiResult(SolveQuestResult(a))
        }
    }
  }

  //  def getQuestSolutionHelpCost(request: GetQuestSolutionHelpCostRequest): ApiResult[GetQuestSolutionHelpCostResult] = handleDbException {
  //    import request._
  //
  //    OkApiResult(GetQuestSolutionHelpCostResult(OK, Some(user.costOfAskingForHelpWithSolution)))
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

}

