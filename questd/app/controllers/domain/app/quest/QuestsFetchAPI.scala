package controllers.domain.app.quest

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import play.Logger

case class GetFriendsQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None)
case class GetFriendsQuestsResult(quests: Iterator[Quest])

case class GetShortlistQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None)
case class GetShortlistQuestsResult(quests: Iterator[Quest])

case class GetLikedQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None)
case class GetLikedQuestsResult(quests: Iterator[Quest])

case class GetVIPQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String])
case class GetVIPQuestsResult(quests: Iterator[Quest])

case class GetAllQuestsRequest(status: QuestStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String] = List())
case class GetAllQuestsResult(quests: Iterator[Quest])

private[domain] trait QuestsFetchAPI { this: DBAccessor =>

  def getFriendsQuests(request: GetFriendsQuestsRequest): ApiResult[GetFriendsQuestsResult] = handleDbException {
    OkApiResult(Some(GetFriendsQuestsResult(db.quest.allWithParams(
      Some(request.status.toString),
      request.user.friends.filter(_.status == FriendshipStatus.Accepted.toString).map(_.friendId),
      request.levels))))
  }

  def getShortlistQuests(request: GetShortlistQuestsRequest): ApiResult[GetShortlistQuestsResult] = handleDbException {
    OkApiResult(Some(GetShortlistQuestsResult(db.quest.allWithParams(
      Some(request.status.toString),
      request.user.shortlist,
      request.levels))))
  }

  def getLikedQuests(request: GetLikedQuestsRequest): ApiResult[GetLikedQuestsResult] = handleDbException {
    import models.store.mongo.helpers._

    val ids = request.user.history.likedQuestProposalIds.mongoFlatten

    OkApiResult(Some(GetLikedQuestsResult(db.quest.allWithParams(
      status = Some(request.status.toString),
      levels = request.levels,
      ids = ids))))
  }

  def getVIPQuests(request: GetVIPQuestsRequest): ApiResult[GetVIPQuestsResult] = handleDbException {
    OkApiResult(Some(GetVIPQuestsResult(db.quest.allWithParams(
      status = Some(request.status.toString),
      levels = request.levels,
      vip = Some(true),
      themeIds = request.themeIds))))
  }

  def getAllQuests(request: GetAllQuestsRequest): ApiResult[GetAllQuestsResult] = handleDbException {
    Logger.trace("getAllQuests - " + request.toString)

    OkApiResult(Some(GetAllQuestsResult(db.quest.allWithParams(
      status = Some(request.status.toString),
      levels = request.levels,
      themeIds = request.themeIds))))
  }

}


