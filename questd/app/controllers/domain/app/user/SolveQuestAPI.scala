package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.quest.SolveQuestUpdateRequest
import controllers.domain.helpers._
import models.domain._
import models.domain.view.QuestView
import play.Logger

import scala.language.postfixOps

case class SolveQuestRequest(
  user: User,
  questId: String,
  solution: SolutionInfoContent)
case class SolveQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class RewardSolutionAuthorRequest(solution: Solution, author: User, battle: Option[Battle] = None)
case class RewardSolutionAuthorResult()

case class BookmarkQuestRequest(user: User, questId: String)
case class BookmarkQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)


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

            import request.{user => u}

            require(u.demo.cultureId != None)

            // creating solution.
            val culture = u.demo.cultureId.get

            val sol = Solution(
              cultureId = culture,
              questLevel = questToSolve.info.level,
              info = SolutionInfo(
                content = solution,
                authorId = user.id,
                questId = questToSolve.id,
                vip = user.profile.publicProfile.vip))

            db.solution.create(sol)

            // Running db actions
            runWhileSome(u)(
            { u: User =>
              db.user.recordQuestSolving(
                u.id,
                questToSolve.id,
                u.profile.questSolutionContext.bookmarkedQuest.map(_.id) == Some(questToSolve.id))
            }, { u: User =>
              db.user.recordSolutionCreation(
                u.id,
                sol.id)
            }) ifSome { u =>

              // Running API actions
              // Adjusting assets for solving quests.
              adjustAssets(AdjustAssetsRequest(
                user = u,
                cost = Some(questToSolve.info.solveCost)))
            } ifOk { r =>
              makeTask(MakeTaskRequest(r.user, taskType = Some(TaskType.CreateSolution)))
            } ifOk { r =>
              addToTimeLine(AddToTimeLineRequest(
                user = r.user,
                reason = TimeLineReason.Created,
                objectType = TimeLineType.Solution,
                objectId = sol.id))
            } ifOk { r =>
              addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                user = r.user,
                reason = TimeLineReason.Created,
                objectType = TimeLineType.Solution,
                objectId = sol.id))
              //                } ifOk { r =>
              //                  addToMustVoteSolutions(AddToMustVoteSolutionsRequest(u, request.friendsToHelp, solution.id))
            } ifOk { r =>
              {
                val numberOfReviewedQuests = u.timeLine.count { te =>
                  ((te.objectType == TimeLineType.Quest)
                    && (te.actorId != u.id || te.reason != TimeLineReason.Created))
                }
                val numberOfSolvedQuests = u.timeLine.count { te =>
                  ((te.objectType == TimeLineType.Solution)
                    && (te.reason == TimeLineReason.Created)
                    && (te.actorId == u.id))
                }

                // Updating quest points.
                val ratio = if (numberOfSolvedQuests == 0)
                  1
                else
                  Math.round(numberOfReviewedQuests / numberOfSolvedQuests)
                solveQuestUpdate(SolveQuestUpdateRequest(questToSolve, ratio))
              } ifOk {
                tryCreateBattle(TryCreateBattleRequest(sol))
              } ifOk {
                OkApiResult(SolveQuestResult(OK, Some(r.user.profile)))
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

    Logger.debug("API - rewardSolutionAuthor")

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
          InternalErrorApiResult("We are rewarding player for solution what is waiting for competitor")

        case SolutionStatus.OnVoting =>
          InternalErrorApiResult("We are rewarding player for solution what is on voting.")

        case SolutionStatus.Won =>
          storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(
            user = author,
            solution = request.solution,
            battle = request.battle,
            reward = Some(q.info.solveRewardWon)))

        case SolutionStatus.Lost =>
          storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(
            user = author,
            solution = request.solution,
            battle = request.battle,
            reward = Some(q.info.solveRewardLost)))

        case SolutionStatus.CheatingBanned =>
          storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(
            user = author,
            solution = request.solution,
            penalty = Some(q.penaltyForCheatingSolution)))
          removeFromTimeLine(RemoveFromTimeLineRequest(author, request.solution.id))

        case SolutionStatus.IACBanned =>
          storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(
            user = author,
            solution = request.solution,
            penalty = Some(q.penaltyForIACSolution)))
          removeFromTimeLine(RemoveFromTimeLineRequest(author, request.solution.id))
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
   * Bookmark a quest to solve it later.
   */
  def bookmarkQuest(request: BookmarkQuestRequest): ApiResult[BookmarkQuestResult] = handleDbException {
    import request._

    db.quest.readById(questId) ifSome { quest =>
      db.user.setQuestBookmark(user.id, QuestView(quest.id, quest.info)) ifSome { updatedUser =>
        OkApiResult(BookmarkQuestResult(OK, Some(updatedUser.profile)))
      }
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

