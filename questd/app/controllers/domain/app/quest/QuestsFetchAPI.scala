package controllers.domain.app.quest

import components.DBAccessor
import models.domain._
import controllers.domain.helpers._
import controllers.domain._
import play.Logger

case class GetFriendsQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None)
case class GetFriendsQuestsResult(quests: Iterator[Quest])

case class GetFollowingQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None)
case class GetFollowingQuestsResult(quests: Iterator[Quest])

case class GetLikedQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None)
case class GetLikedQuestsResult(quests: Iterator[Quest])

case class GetVIPQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String])
case class GetVIPQuestsResult(quests: Iterator[Quest])

case class GetAllQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String] = List())
case class GetAllQuestsResult(quests: Iterator[Quest])

private[domain] trait QuestsFetchAPI { this: DBAccessor =>

  def getFriendsQuests(request: GetFriendsQuestsRequest): ApiResult[GetFriendsQuestsResult] = handleDbException {
    OkApiResult(GetFriendsQuestsResult(db.quest.allWithParams(
      status = List(request.status.toString),
      authorIds = request.user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId),
      levels = request.levels,
      cultureId = request.user.demo.cultureId)))
  }

  def getFollowingQuests(request: GetFollowingQuestsRequest): ApiResult[GetFollowingQuestsResult] = handleDbException {
    OkApiResult(GetFollowingQuestsResult(db.quest.allWithParams(
      status = List(request.status.toString),
      authorIds = request.user.following,
      levels = request.levels,
      cultureId = request.user.demo.cultureId)))
  }

  def getLikedQuests(request: GetLikedQuestsRequest): ApiResult[GetLikedQuestsResult] = handleDbException {
    import models.store.mongo.helpers._

    val ids = request.user.history.likedQuestProposalIds.mongoFlatten

    OkApiResult(GetLikedQuestsResult(db.quest.allWithParams(
      status = List(request.status.toString),
      levels = request.levels,
      ids = ids,
      cultureId = request.user.demo.cultureId)))
  }

  def getVIPQuests(request: GetVIPQuestsRequest): ApiResult[GetVIPQuestsResult] = handleDbException {
    OkApiResult(GetVIPQuestsResult(db.quest.allWithParams(
      status = List(request.status.toString),
      levels = request.levels,
      vip = Some(true),
      themeIds = request.themeIds,
      cultureId = request.user.demo.cultureId)))
  }

  def getAllQuests(request: GetAllQuestsRequest): ApiResult[GetAllQuestsResult] = handleDbException {
    Logger.trace("getAllQuests - " + request.toString)

    OkApiResult(GetAllQuestsResult(db.quest.allWithParams(
      status = List(request.status.toString),
      levels = request.levels,
      themeIds = request.themeIds,
      cultureId = request.user.demo.cultureId)))
  }

}
