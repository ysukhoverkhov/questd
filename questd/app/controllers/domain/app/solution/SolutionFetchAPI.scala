package controllers.domain.app.solution

import components.DBAccessor
import controllers.domain._
import controllers.domain.helpers._
import models.domain.common.ContentVote
import models.domain.solution.{Solution, SolutionStatus}
import models.domain.user.User
import models.domain.user.friends.FriendshipStatus

case class GetFriendsSolutionsRequest(
  user: User,
  status: List[SolutionStatus.Value],
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetFriendsSolutionsResult(solutions: Iterator[Solution])

case class GetFollowingSolutionsRequest(
  user: User,
  status: List[SolutionStatus.Value],
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetFollowingSolutionsResult(solutions: Iterator[Solution])

case class GetSolutionsForLikedQuestsRequest(
  user: User,
  status: List[SolutionStatus.Value],
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetSolutionsForLikedQuestsResult(solutions: Iterator[Solution])

case class GetVIPSolutionsRequest(
  user: User,
  status: List[SolutionStatus.Value],
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None,
  themeIds: List[String] = List.empty)
case class GetVIPSolutionsResult(solutions: Iterator[Solution])

case class GetHelpWantedSolutionsRequest(
  user: User,
  status: List[SolutionStatus.Value],
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetHelpWantedSolutionsResult(solutions: Iterator[Solution])

case class GetSolutionsForOwnQuestsRequest(
  user: User,
  status: List[SolutionStatus.Value],
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetSolutionsForOwnQuestsResult(solutions: Iterator[Solution])

case class GetAllSolutionsRequest(
  user: User,
  status: List[SolutionStatus.Value],
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None,
  themeIds: List[String] = List.empty,
  cultureId: Option[String])
case class GetAllSolutionsResult(solutions: Iterator[Solution])


private[domain] trait SolutionFetchAPI { this: DBAccessor =>

  def getFriendsSolutions(request: GetFriendsSolutionsRequest): ApiResult[GetFriendsSolutionsResult] = handleDbException {
    OkApiResult(GetFriendsSolutionsResult(db.solution.allWithParams(
      status = request.status,
      authorIds = request.user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId),
      authorIdsExclude = request.authorsExclude,
      idsExclude = request.idsExclude,
      levels = request.levels,
      cultureId = request.user.demo.cultureId)))
  }

  def getFollowingSolutions(request: GetFollowingSolutionsRequest): ApiResult[GetFollowingSolutionsResult] = handleDbException {
    OkApiResult(GetFollowingSolutionsResult(db.solution.allWithParams(
      status = request.status,
      authorIds = request.user.following,
      authorIdsExclude = request.authorsExclude,
      idsExclude = request.idsExclude,
      levels = request.levels,
      cultureId = request.user.demo.cultureId)))
  }

  def getSolutionsForLikedQuests(request: GetSolutionsForLikedQuestsRequest): ApiResult[GetSolutionsForLikedQuestsResult] = handleDbException {
    import request._

    val ids = user.stats.votedQuests.filter(_._2 == ContentVote.Cool).keys.toList

    OkApiResult(GetSolutionsForLikedQuestsResult(db.solution.allWithParams(
      status = request.status,
      authorIdsExclude = request.authorsExclude,
      idsExclude = request.idsExclude,
      levels = request.levels,
      questIds = ids,
      cultureId = request.user.demo.cultureId)))
  }

  def getVIPSolutions(request: GetVIPSolutionsRequest): ApiResult[GetVIPSolutionsResult] = handleDbException {
    OkApiResult(GetVIPSolutionsResult(db.solution.allWithParams(
      status = request.status,
      authorIdsExclude = request.authorsExclude,
      idsExclude = request.idsExclude,
      levels = request.levels,
      vip = Some(true),
      themeIds = request.themeIds,
      cultureId = request.user.demo.cultureId)))
  }

  def getHelpWantedSolutions(request: GetHelpWantedSolutionsRequest): ApiResult[GetHelpWantedSolutionsResult] = handleDbException {
    if (request.user.mustVoteSolutions.isEmpty) {
      OkApiResult(GetHelpWantedSolutionsResult(List.empty.iterator))
    } else {
      OkApiResult(GetHelpWantedSolutionsResult(db.solution.allWithParams(
        status = request.status,
        authorIdsExclude = request.authorsExclude,
        idsExclude = request.idsExclude,
        levels = request.levels,
        cultureId = request.user.demo.cultureId,
        ids = request.user.mustVoteSolutions)))
    }
  }

  def getSolutionsForOwnQuests(request: GetSolutionsForOwnQuestsRequest): ApiResult[GetSolutionsForOwnQuestsResult] = handleDbException {

    val questIds = db.quest.allWithParams(authorIds = List(request.user.id)).toList.map(_.id)

    if (questIds.nonEmpty) {
      OkApiResult(GetSolutionsForOwnQuestsResult(db.solution.allWithParams(
        status = request.status,
        authorIdsExclude = request.authorsExclude,
        idsExclude = request.idsExclude,
        levels = request.levels,
        cultureId = request.user.demo.cultureId,
        questIds = questIds)))
    } else {
      OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
    }
  }

  def getAllSolutions(request: GetAllSolutionsRequest): ApiResult[GetAllSolutionsResult] = handleDbException {

    OkApiResult(GetAllSolutionsResult(db.solution.allWithParams(
      status = request.status,
      authorIdsExclude = request.authorsExclude,
      idsExclude = request.idsExclude,
      levels = request.levels,
      themeIds = request.themeIds,
      cultureId = request.cultureId)))
  }
}
