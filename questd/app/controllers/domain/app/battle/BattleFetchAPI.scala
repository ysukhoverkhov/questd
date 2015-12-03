package controllers.domain.app.battle

import components.DBAccessor
import controllers.domain.helpers._
import controllers.domain.{ApiResult, OkApiResult}
import models.domain.battle.{Battle, BattleStatus}
import models.domain.common.ContentVote
import models.domain.user.User
import models.domain.user.friends.FriendshipStatus

case class GetAllBattlesInternalRequest()
case class GetAllBattlesInternalResult(battles: Iterator[Battle])

case class GetFriendsBattlesRequest(
  user: User,
  statuses: List[BattleStatus.Value] = List.empty,
  idsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetFriendsBattlesResult(battles: Iterator[Battle])

case class GetFollowingBattlesRequest(
  user: User,
  statuses: List[BattleStatus.Value] = List.empty,
  idsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetFollowingBattlesResult(battles: Iterator[Battle])

case class GetVIPBattlesRequest(
  user: User,
  statuses: List[BattleStatus.Value] = List.empty,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetVIPBattlesResult(battles: Iterator[Battle])

case class GetLikedSolutionBattlesRequest(
  user: User,
  statuses: List[BattleStatus.Value] = List.empty,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetLikedSolutionBattlesResult(battles: Iterator[Battle])

case class GetAllBattlesRequest(
  user: User,
  statuses: List[BattleStatus.Value] = List.empty,
  authorIdsExclude: List[String] = List.empty,
  idsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetAllBattlesResult(battles: Iterator[Battle])

private[domain] trait BattleFetchAPI { this: DBAccessor =>

  /**
   * Get all battles in fighting state. used internally.
   */
  def getAllBattlesInternal(request: GetAllBattlesInternalRequest): ApiResult[GetAllBattlesInternalResult] = handleDbException {
    OkApiResult(GetAllBattlesInternalResult(db.battle.allWithParams(
      status = List(BattleStatus.Fighting))))
  }

  def getFriendsBattles(request: GetFriendsBattlesRequest): ApiResult[GetFriendsBattlesResult] = handleDbException {
    OkApiResult(GetFriendsBattlesResult(db.battle.allWithParams(
      status = request.statuses,
      authorIds = request.user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId),
      levels = request.levels,
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId)))
  }

  def getFollowingBattles(request: GetFollowingBattlesRequest): ApiResult[GetFollowingBattlesResult] = handleDbException {
    OkApiResult(GetFollowingBattlesResult(db.battle.allWithParams(
      status = request.statuses,
      authorIds = request.user.following,
      levels = request.levels,
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId)))
  }

  def getVIPBattles(request: GetVIPBattlesRequest): ApiResult[GetVIPBattlesResult] = handleDbException {
    OkApiResult(GetVIPBattlesResult(db.battle.allWithParams(
      status = request.statuses,
      authorIdsExclude = request.authorsExclude,
      levels = request.levels,
      vip = Some(true),
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId)))
  }

  def getLikedSolutionBattles(request: GetLikedSolutionBattlesRequest): ApiResult[GetLikedSolutionBattlesResult] = handleDbException {
    OkApiResult(GetLikedSolutionBattlesResult(db.battle.allWithParams(
      status = request.statuses,
      authorIdsExclude = request.authorsExclude,
      solutionIds = request.user.stats.votedSolutions.filter(_._2 == ContentVote.Cool).keys.toList,
      levels = request.levels,
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId)))
  }

  /**
   * Returns battles with params.
   * @param request The request.
   * @return
   */
  def getAllBattles(request: GetAllBattlesRequest): ApiResult[GetAllBattlesResult] = handleDbException {
    OkApiResult(GetAllBattlesResult(db.battle.allWithParams(
      status = request.statuses,
      levels = request.levels,
      authorIdsExclude = request.authorIdsExclude,
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId)))
  }
}
