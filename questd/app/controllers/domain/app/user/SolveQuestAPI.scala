package controllers.domain.app.user


import logic.BattleLogic
import scala.language.postfixOps
import models.domain._
import play.Logger
import controllers.domain.app.quest.SolveQuestUpdateRequest
import controllers.domain.helpers._
import controllers.domain._
import components._
import controllers.domain.app.protocol.ProfileModificationResult._

case class SolveQuestRequest(
  user: User,
  questId: String,
  solution: SolutionInfoContent)
case class SolveQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class TryFindCompetitorRequest(solution: Solution)
case class TryFindCompetitorResult()

case class RewardSolutionAuthorRequest(solution: Solution, author: User)
case class RewardSolutionAuthorResult()

case class TryFightQuestRequest(solution: Solution)
case class TryFightQuestResult()


//case class GetQuestSolutionHelpCostRequest(user: User)
//case class GetQuestSolutionHelpCostResult(allowed: ProfileModificationResult, cost: Option[Assets] = None)

//case class AddToMustVoteSolutionsRequest(user: User, friendIds: List[String], solutionId: String)
//case class AddToMustVoteSolutionsResult(user: User)

private[domain] trait SolveQuestAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>



  /**
   * Solve a quest.
   */
  def solveQuest(request: SolveQuestRequest): ApiResult[SolveQuestResult] = handleDbException {
    import request._

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

            user.demo.cultureId ifSome { culture =>

              val solution = Solution(
                cultureId = culture,
                questLevel = questToSolve.info.level,
                info = SolutionInfo(
                  content = content,
                  authorId = user.id,
                  questId = questToSolve.id,
                  vip = user.profile.publicProfile.vip))

              {
                // Adjusting assets for solving quests.
                adjustAssets(AdjustAssetsRequest(
                  user = user,
                  cost = Some(questToSolve.info.solveCost)))
              } ifOk { r =>
                makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.SubmitQuestResult)))
              } ifOk { r =>

                // Creating solution.
                db.solution.create(solution)

                val numberOfReviewedQuests = user.timeLine.count { te =>
                  ((te.objectType == TimeLineType.Quest)
                    && (te.objectAuthorId != user.id || te.reason != TimeLineReason.Created))
                }
                val numberOfSolvedQuests = user.timeLine.count { te =>
                  ((te.objectType == TimeLineType.Solution)
                    && (te.reason == TimeLineReason.Created)
                    && (te.objectAuthorId == user.id))
                }

                // Updating quest points.
                val ratio = if (numberOfSolvedQuests == 0)
                  1
                else
                  Math.round(numberOfReviewedQuests / numberOfSolvedQuests)
                solveQuestUpdate(SolveQuestUpdateRequest(questToSolve, ratio))
              } ifOk { sqr =>
                if (user.profile.questSolutionContext.bookmarkedQuest.map(_.id) == Some(questToSolve.id))
                  db.user.recordQuestSolving(user.id, questToSolve.id)

                addToTimeLine(AddToTimeLineRequest(
                  user = user,
                  reason = TimeLineReason.Created,
                  objectType = TimeLineType.Solution,
                  objectId = solution.id))
              } ifOk { r =>
                addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                  user = r.user,
                  reason = TimeLineReason.Created,
                  objectType = TimeLineType.Solution,
                  objectId = solution.id))
                //                } ifOk { r =>
                //                  addToMustVoteSolutions(AddToMustVoteSolutionsRequest(u, request.friendsToHelp, solution.id))
              } ifOk { r =>
                tryCreateBattle(TryFindCompetitorRequest(solution)) ifOk {
                  OkApiResult(SolveQuestResult(OK, Some(r.user.profile)))
                }
              }
            }

          case (a: ProfileModificationResult) => OkApiResult(SolveQuestResult(a))
        }
    }
  }

  /**
   * Tries to match ques with competitor, leaves it as it is if not found.
   * @param request Request with solution to find competitor for.
   * @return Result of competitor search.
   */
  // TODO: move it to API dedicated to battles.
  def tryCreateBattle(request: TryFindCompetitorRequest): ApiResult[TryFindCompetitorResult] = handleDbException {
    import request._

    def selectCompetitor(possibleCompetitors: Iterator[Solution]): Option[Solution] = {
      if (possibleCompetitors.hasNext) {
        val other = possibleCompetitors.next()

        if (other.info.authorId != request.solution.info.authorId) {

          Logger.debug("Found fight pair for quest " + request.solution.info.questId + " :")
          Logger.debug("  s1.id=" + request.solution.id)
          Logger.debug("  s2.id=" + other.id)

          Some(other)

        } else {
          // Skipping to next if current is we are.
          selectCompetitor(possibleCompetitors)
        }
      } else {
        None
      }
    }

    val possibleCompetitors = db.solution.allWithParams(
      status = List(SolutionStatus.WaitingForCompetitor),
      questIds = List(solution.info.questId),
      cultureId = Some(solution.cultureId))

    selectCompetitor(possibleCompetitors) match {
      case Some(competitor) =>
        // FIX: transaction should be here as this operation is atomic.
        val battle = Battle(
          solutionIds = List(solution.id, competitor.id),
          voteEndDate = BattleLogic.voteEndDate(solution.questLevel)
        )

        // TODO: store battle in solutions


        battle.solutionIds.foreach {
          db.solution.updateStatus(_, SolutionStatus.OnVoting)
        }

        db.battle.create(battle)

        OkApiResult(TryFindCompetitorResult())

      case None =>
        OkApiResult(TryFindCompetitorResult())
    }
  }


  /**
   * Give quest solution author a reward on quest status change
   */
  def rewardSolutionAuthor(request: RewardSolutionAuthorRequest): ApiResult[RewardSolutionAuthorResult] = handleDbException {
    import request._

    Logger.debug("API - rewardQuestSolutionAuthor")

    class QuestNotFoundException() extends Throwable

    def q = {
      db.quest.readById(solution.info.questId) match {
        case Some(qu) => qu
        case None => throw new QuestNotFoundException()
      }
    }

    try {
      val r = solution.status match {
          // FIX: implement this part.
//        case QuestSolutionStatus.OnVoting =>
//          Logger.error("We are rewarding player for solution what is on voting.")
//          InternalErrorApiResult()
//
//        case QuestSolutionStatus.WaitingForCompetitor =>
//          tryFightQuest(TryFightQuestRequest(solution)) ifOk OkApiResult(StoreSolutionInDailyResultResult(author))

        case SolutionStatus.Won =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution, reward = Some(q.info.solveRewardWon)))

        case SolutionStatus.Lost =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution, reward = Some(q.info.solveRewardLost)))

        case SolutionStatus.CheatingBanned =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution, penalty = Some(q.penaltyForCheatingSolution)))

        case SolutionStatus.IACBanned =>
            storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(author, request.solution, penalty = Some(q.penaltyForIACSolution)))
      }

      r ifOk {
        OkApiResult(RewardSolutionAuthorResult())
      }
    } catch {
      case ex: QuestNotFoundException =>
        Logger.error("No quest found for updating player assets for changing solution state.")
        InternalErrorApiResult()
    }
  }

  /**
   * Tries to find competitor to us on quest and resolve our battle. Updates db after that.
   */
  // TODO: clean me up.
//  def tryFightQuest(request: TryFightQuestRequest): ApiResult[TryFightQuestResult] = handleDbException {
//    // 1. find all solutions with the same quest id with status waiting for competitor.
//
//    val possibleCompetitors = db.solution.allWithParams(
//      status = List(SolutionStatus.WaitingForCompetitor),
//      questIds = List(request.solution.info.questId))
//
//    def fight(s1: Solution, s2: Solution): (List[Solution], List[Solution]) = {
//      if (s1.calculatePoints == s2.calculatePoints)
//        (List(s1, s2), List())
//      else if (s1.calculatePoints > s2.calculatePoints)
//        (List(s1), List(s2))
//      else
//        (List(s2), List(s1))
//    }
//
//    @tailrec
//    def compete(solutions: Iterator[Solution]): ApiResult[TryFightQuestResult] = {
//      if (solutions.hasNext) {
//        val other = solutions.next()
//
//        if (other.info.authorId != request.solution.info.authorId) {
//
//          Logger.debug("Found fight pair for quest " + request.solution + ":")
//          Logger.debug("  s1.id=" + request.solution.id)
//          Logger.debug("  s2.id=" + other.id)
//
//          // Updating solution rivals
//          val ourSol = request.solution.copy(rivalSolutionId = Some(other.id))
//          val otherSol = other.copy(rivalSolutionId = Some(request.solution.id))
//
//          // Compare two solutions.
//          val (winners, losers) = fight(otherSol, ourSol)
//
//          // update solutions, winners
//          for (curSol <- winners) {
//            Logger.debug("  winner id=" + curSol.id)
//
//            db.solution.updateStatus(curSol.id, SolutionStatus.Won, curSol.rivalSolutionId) ifSome { s =>
//              db.user.readById(curSol.info.authorId) ifSome { u =>
//                rewardSolutionAuthor(RewardSolutionAuthorRequest(solution = s, author = u))
//              }
//            }
//
//          }
//
//          // and losers
//          for (curSol <- losers) {
//            Logger.debug("  loser id=" + curSol.id)
//
//            db.solution.updateStatus(curSol.id, SolutionStatus.Lost, curSol.rivalSolutionId) ifSome { s =>
//              db.user.readById(curSol.info authorId) ifSome { u =>
//                rewardSolutionAuthor(RewardSolutionAuthorRequest(solution = s, author = u))
//              }
//            }
//
//          }
//
//          OkApiResult(TryFightQuestResult())
//
//        } else {
//
//          // Skipping to next if current is we are.
//          compete(solutions)
//        }
//      } else {
//
//        // We didn;t find competitor but this is ok.
//        OkApiResult(TryFightQuestResult())
//      }
//    }
//
//    compete(possibleCompetitors)
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


  //  def getQuestSolutionHelpCost(request: GetQuestSolutionHelpCostRequest): ApiResult[GetQuestSolutionHelpCostResult] = handleDbException {
  //    import request._
  //
  //    OkApiResult(GetQuestSolutionHelpCostResult(OK, Some(user.costOfAskingForHelpWithSolution)))
  //  }

}

