package controllers.domain.app.quest

import components.DBAccessor
import controllers.domain._
import controllers.domain.helpers._
import models.domain.quest.{Quest, QuestStatus}
import models.domain.user.User
import models.domain.user.friends.FriendshipStatus

case class GetMyQuestsRequest(
  user: User,
  status: QuestStatus.Value)
case class GetMyQuestsResult(quests: Iterator[Quest])

case class GetFriendsQuestsRequest(
  user: User,
  status: QuestStatus.Value,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  levels: Option[(Int, Int)] = None,
  withSolutions: Option[Boolean] = None)
case class GetFriendsQuestsResult(quests: Iterator[Quest])

case class GetFollowingQuestsRequest(
  user: User,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  status: QuestStatus.Value,
  levels: Option[(Int, Int)] = None,
  withSolutions: Option[Boolean] = None)
case class GetFollowingQuestsResult(quests: Iterator[Quest])

case class GetVIPQuestsRequest(
  user: User,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  status: QuestStatus.Value,
  levels: Option[(Int, Int)] = None,
  withSolutions: Option[Boolean] = None)
case class GetVIPQuestsResult(quests: Iterator[Quest])

case class GetAllQuestsRequest(
  user: User,
  ids: List[String] = List.empty,
  idsExclude: List[String] = List.empty,
  authorsExclude: List[String] = List.empty,
  status: QuestStatus.Value,
  levels: Option[(Int, Int)] = None,
  cultureId: Option[String],
  withSolutions: Option[Boolean] = None)
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
      cultureId = request.user.demo.cultureId,
      withSolutions = request.withSolutions)))
  }

  def getFollowingQuests(request: GetFollowingQuestsRequest): ApiResult[GetFollowingQuestsResult] = handleDbException {
    OkApiResult(GetFollowingQuestsResult(db.quest.allWithParams(
      status = List(request.status),
      authorIds = request.user.following,
      authorIdsExclude = request.authorsExclude,
      levels = request.levels,
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId,
      withSolutions = request.withSolutions)))
  }

  def getVIPQuests(request: GetVIPQuestsRequest): ApiResult[GetVIPQuestsResult] = handleDbException {
    OkApiResult(GetVIPQuestsResult(db.quest.allWithParams(
      status = List(request.status),
      authorIdsExclude = request.authorsExclude,
      levels = request.levels,
      vip = Some(true),
      idsExclude = request.idsExclude,
      cultureId = request.user.demo.cultureId,
      withSolutions = request.withSolutions)))
  }

  def getAllQuests(request: GetAllQuestsRequest): ApiResult[GetAllQuestsResult] = handleDbException {
    OkApiResult(GetAllQuestsResult(db.quest.allWithParams(
      status = List(request.status),
      authorIdsExclude = request.authorsExclude,
      levels = request.levels,
      ids = request.ids,
      idsExclude = request.idsExclude,
      cultureId = request.cultureId,
      withSolutions = request.withSolutions)))
  }
}
