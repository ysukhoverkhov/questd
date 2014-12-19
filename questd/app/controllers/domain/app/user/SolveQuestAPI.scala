package controllers.domain.app.user

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
                makeTask(MakeTaskRequest(request.user, taskType = Some(TaskType.CreateSolution)))
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
                tryCreateBattle(TryCreateBattleRequest(solution)) ifOk {
                  OkApiResult(SolveQuestResult(OK, Some(r.user.profile)))
                }
              }
            }

          case (a: ProfileModificationResult) => OkApiResult(SolveQuestResult(a))
        }
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
        case SolutionStatus.WaitingForCompetitor =>
          InternalErrorApiResult("We are rewarding player for solution what is waitin for competitor")

        case SolutionStatus.OnVoting =>
          InternalErrorApiResult("We are rewarding player for solution what is on voting.")

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
        InternalErrorApiResult("No quest found for updating player assets for changing solution state")
    }
  }

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

