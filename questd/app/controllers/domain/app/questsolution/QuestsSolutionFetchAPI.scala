package controllers.domain.app.questsolution

import components.DBAccessor
import models.domain._
import controllers.domain.helpers._
import controllers.domain._
import play.Logger

case class GetFriendsSolutionsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None)
case class GetFriendsSolutionsResult(solutions: Iterator[QuestSolution])

case class GetShortlistSolutionsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None)
case class GetShortlistSolutionsResult(solutions: Iterator[QuestSolution])

case class GetSolutionsForLikedQuestsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None)
case class GetSolutionsForLikedQuestsResult(solutions: Iterator[QuestSolution])

case class GetVIPSolutionsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String])
case class GetVIPSolutionsResult(solutions: Iterator[QuestSolution])

case class GetHelpWantedSolutionsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None)
case class GetHelpWantedSolutionsResult(solutions: Iterator[QuestSolution])

case class GetAllSolutionsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String] = List())
case class GetAllSolutionsResult(solutions: Iterator[QuestSolution])


private[domain] trait QuestsSolutionFetchAPI { this: DBAccessor =>

  def getFriendsSolutions(request: GetFriendsSolutionsRequest): ApiResult[GetFriendsSolutionsResult] = handleDbException {
    OkApiResult(GetFriendsSolutionsResult(db.solution.allWithParams(
      status = List(request.status.toString),
      authorIds = request.user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId),
      levels = request.levels,
      cultureId = request.user.demo.cultureId)))
  }

  def getShortlistSolutions(request: GetShortlistSolutionsRequest): ApiResult[GetShortlistSolutionsResult] = handleDbException {
    OkApiResult(GetShortlistSolutionsResult(db.solution.allWithParams(
      status = List(request.status.toString),
      authorIds = request.user.shortlist,
      levels = request.levels,
      cultureId = request.user.demo.cultureId)))
  }

  def getSolutionsForLikedQuests(request: GetSolutionsForLikedQuestsRequest): ApiResult[GetSolutionsForLikedQuestsResult] = handleDbException {
    import models.store.mongo.helpers._

    OkApiResult(GetSolutionsForLikedQuestsResult(db.solution.allWithParams(
      status = List(request.status.toString),
      levels = request.levels,
      questIds = request.user.history.likedQuestProposalIds.mongoFlatten,
      cultureId = request.user.demo.cultureId)))
  }

  def getVIPSolutions(request: GetVIPSolutionsRequest): ApiResult[GetVIPSolutionsResult] = handleDbException {
    OkApiResult(GetVIPSolutionsResult(db.solution.allWithParams(
      status = List(request.status.toString),
      levels = request.levels,
      vip = Some(true),
      themeIds = request.themeIds,
      cultureId = request.user.demo.cultureId)))
  }

  def getHelpWantedSolutions(request: GetHelpWantedSolutionsRequest): ApiResult[GetHelpWantedSolutionsResult] = handleDbException {
    if (request.user.mustVoteSolutions.isEmpty) {
      OkApiResult(GetHelpWantedSolutionsResult(List().iterator))
    } else {
      OkApiResult(GetHelpWantedSolutionsResult(db.solution.allWithParams(
        status = List(request.status.toString),
        levels = request.levels,
        cultureId = request.user.demo.cultureId,
        ids = request.user.mustVoteSolutions)))
    }
  }

  def getAllSolutions(request: GetAllSolutionsRequest): ApiResult[GetAllSolutionsResult] = handleDbException {
    Logger.trace("getAllSolutions - " + request.toString)

    OkApiResult(GetAllSolutionsResult(db.solution.allWithParams(
      status = List(request.status.toString),
      levels = request.levels,
      themeIds = request.themeIds,
      cultureId = request.user.demo.cultureId)))
  }
}
