package controllers.domain.app.quest

import components.DBAccessor
import models.domain._
import controllers.domain.helpers._
import controllers.domain._
import play.Logger

case class GetMyQuestsRequest(
  user: User,
  status: QuestStatus.Value)
case class GetMyQuestsResult(quests: Iterator[Quest])

case class GetFriendsQuestsRequest(
  user: User,
  status: QuestStatus.Value,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetFriendsQuestsResult(quests: Iterator[Quest])

case class GetFollowingQuestsRequest(
  user: User,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  status: QuestStatus.Value,
  levels: Option[(Int, Int)] = None)
case class GetFollowingQuestsResult(quests: Iterator[Quest])

case class GetVIPQuestsRequest(
  user: User,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  status: QuestStatus.Value,
  levels: Option[(Int, Int)] = None)
case class GetVIPQuestsResult(quests: Iterator[Quest])

case class GetAllQuestsRequest(
  user: User,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  status: QuestStatus.Value,
  levels: Option[(Int, Int)] = None,
  cultureId: Option[String])
case class GetAllQuestsResult(quests: Iterator[Quest])

private[domain] trait QuestsFetchAPI { this: DBAccessor =>

  def getMyQuests(request: GetMyQuestsRequest): ApiResult[GetMyQuestsResult] = handleDbException {
    OkApiResult(GetMyQuestsResult(db.quest.allWithParams(
      status = List(request.status),
      authorIds = List(request.user.id))))
  }

  def getFriendsQuests(request: GetFriendsQuestsRequest): ApiResult[GetFriendsQuestsResult] = handleDbException {
    OkApiResult(GetFriendsQuestsResult(db.quest.allWithParams(
      status = List(request.status),
      authorIds = request.user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId),
      authorIdsExclude = request.authorsExclude,
      levels = request.levels,
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId)))
  }

  def getFollowingQuests(request: GetFollowingQuestsRequest): ApiResult[GetFollowingQuestsResult] = handleDbException {
    OkApiResult(GetFollowingQuestsResult(db.quest.allWithParams(
      status = List(request.status),
      authorIds = request.user.following,
      authorIdsExclude = request.authorsExclude,
      levels = request.levels,
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId)))
  }

  def getVIPQuests(request: GetVIPQuestsRequest): ApiResult[GetVIPQuestsResult] = handleDbException {
    OkApiResult(GetVIPQuestsResult(db.quest.allWithParams(
      status = List(request.status),
      authorIdsExclude = request.authorsExclude,
      levels = request.levels,
      vip = Some(true),
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId)))
  }

  def getAllQuests(request: GetAllQuestsRequest): ApiResult[GetAllQuestsResult] = handleDbException {
    Logger.trace("getAllQuests - " + request.toString)

    OkApiResult(GetAllQuestsResult(db.quest.allWithParams(
      status = List(request.status),
      authorIdsExclude = request.authorsExclude,
      levels = request.levels,
      idsExclude = request.idsExclude,
      cultureId = request.cultureId)))
  }
}
