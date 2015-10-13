package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.quest.SolveQuestUpdateRequest
import controllers.domain.helpers._
import models.domain.solution.{Solution, SolutionInfo, SolutionInfoContent, SolutionStatus}
import models.domain.user._
import models.domain.user.profile.{Profile, TaskType}
import models.domain.user.timeline.{TimeLineReason, TimeLineType}
import models.view.{QuestView, SolutionView}
import play.Logger

import scala.language.postfixOps

case class SolveQuestRequest(
  user: User,
  questId: String,
  solution: SolutionInfoContent)
case class SolveQuestResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None,
  modifiedQuests: List[QuestView] = List.empty,
  modifiedSolutions: List[SolutionView] = List.empty)

case class RewardSolutionAuthorRequest(solution: Solution, author: User)
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

            require(u.demo.cultureId.isDefined)

            // creating solution.
            val culture = u.demo.cultureId.get

            val newSolution = Solution(
              cultureId = culture,
              questLevel = questToSolve.info.level,
              info = SolutionInfo(
                content = solution,
                authorId = user.id,
                questId = questToSolve.id,
                vip = user.profile.publicProfile.vip))

            db.solution.create(newSolution)

            // Running db actions
            runWhileSome(u)(
            { u: User =>
              db.user.recordQuestSolving(
                u.id,
                questToSolve.id,
                newSolution.id,
                u.profile.questSolutionContext.bookmarkedQuest.map(_.id).contains(questToSolve.id))
            }) ifSome { u =>

              // Running API actions
              // Adjusting assets for solving quests.
              adjustAssets(AdjustAssetsRequest(
                user = u,
                change = -questToSolve.info.solveCost + questToSolve.info.solveReward))

            } map { r =>
              makeTask(MakeTaskRequest(r.user, taskType = Some(TaskType.CreateSolution)))
            } map { r =>
              addToTimeLine(AddToTimeLineRequest(
                user = r.user,
                reason = TimeLineReason.Created,
                objectType = TimeLineType.Solution,
                objectId = newSolution.id))
            } map { r =>
              addToWatchersTimeLine(AddToWatchersTimeLineRequest(
                user = r.user,
                reason = TimeLineReason.Created,
                objectType = TimeLineType.Solution,
                objectId = newSolution.id))
              //                } ifOk { r =>
              //                  addToMustVoteSolutions(AddToMustVoteSolutionsRequest(u, request.friendsToHelp, solution.id))
            } map { r =>
              {
                val numberOfReviewedQuests = r.user.timeLine.count { te =>
                  ((te.objectType == TimeLineType.Quest)
                    && (te.actorId != r.user.id || te.reason != TimeLineReason.Created))
                }
                val numberOfSolvedQuests = r.user.stats.solvedQuests.size

                // Updating quest points.
                val ratio = if (numberOfSolvedQuests == 0)
                  1
                else
                  Math.max(
                    Math.round(numberOfReviewedQuests / numberOfSolvedQuests),
                    config(DefaultConfigParams.QuestMaxTimeLinePointsForSolve).toInt)

                solveQuestUpdate(SolveQuestUpdateRequest(questToSolve, ratio, newSolution.id))
              } map {

                // Giving reward to author of the quest.
                db.user.readById(questToSolve.info.authorId) ifSome { author =>
                  storeQuestSolvingInDailyResult(StoreQuestSolvingInDailyResultRequest(author, questToSolve))
                }
              } map {
                OkApiResult(SolveQuestResult(
                  allowed = OK,
                  profile = Some(r.user.profile),
                  modifiedQuests = List(QuestView(questToSolve, r.user)),
                  modifiedSolutions = List(SolutionView(newSolution, r.user))))
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
        case SolutionStatus.InRotation =>
          InternalErrorApiResult("We are rewarding player for solution what is in rotation")

        case SolutionStatus.OldBanned =>
          // We do nothing here.
          OkApiResult(RewardSolutionAuthorResult())

        case SolutionStatus.CheatingBanned =>
          storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(
            user = author,
            solution = request.solution,
            reward = -q.penaltyForCheatingSolution))
          removeFromTimeLine(RemoveFromTimeLineRequest(author, request.solution.id))

        case SolutionStatus.IACBanned =>
          storeSolutionInDailyResult(StoreSolutionInDailyResultRequest(
            user = author,
            solution = request.solution,
            reward = -q.penaltyForIACSolution))
          removeFromTimeLine(RemoveFromTimeLineRequest(author, request.solution.id))
      }

      r map {
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
      db.user.setQuestBookmark(
        id = user.id,
        questId = QuestView(quest, user)) ifSome { updatedUser =>
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

