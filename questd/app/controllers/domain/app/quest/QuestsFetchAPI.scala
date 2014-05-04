package controllers.domain.app.quest

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import play.Logger

case class GetFriendsQuestsRequest(user: User, status: QuestStatus.Value, fromLevel: Int, toLevel: Int)
case class GetFriendsQuestsResult(quests: Iterator[Quest])

case class GetShortlistQuestsRequest(user: User, status: QuestStatus.Value, fromLevel: Int, toLevel: Int)
case class GetShortlistQuestsResult(quests: Iterator[Quest])

case class GetVIPQuestsRequest(user: User, status: QuestStatus.Value, fromLevel: Int, toLevel: Int, themeIds: List[String])
case class GetVIPQuestsResult(quests: Iterator[Quest])

case class GetLikedQuestsRequest(user: User, status: QuestStatus.Value, fromLevel: Int, toLevel: Int)
case class GetLikedQuestsResult(quests: Iterator[Quest])

case class GetAllQuestsRequest(status: QuestStatus.Value, fromLevel: Int, toLevel: Int, themeIds: List[String] = List())
case class GetAllQuestsResult(quests: Iterator[Quest])


private [domain] trait QuestsFetchAPI { this: DBAccessor => 

  
  def getFriendsQuests(request: GetFriendsQuestsRequest): ApiResult[GetFriendsQuestsResult] = handleDbException {
    OkApiResult(Some(GetFriendsQuestsResult(db.quest.allWithParams(
      Some(request.status.toString),
      request.user.friends.filter(_.status == FriendshipStatus.Accepted.toString).map(_.friendId),
      Some(request.fromLevel, request.toLevel)))))
  }

  def getShortlistQuests(request: GetShortlistQuestsRequest): ApiResult[GetShortlistQuestsResult] = handleDbException {
    OkApiResult(Some(GetShortlistQuestsResult(db.quest.allWithParams(
      Some(request.status.toString),
      request.user.shortlist,
      Some(request.fromLevel, request.toLevel)))))
  }
  
  def getLikedQuests(request: GetLikedQuestsRequest): ApiResult[GetLikedQuestsResult] = handleDbException {
    OkApiResult(Some(GetLikedQuestsResult(db.quest.allWithParams(
      status = Some(request.status.toString),
      levels = Some(request.fromLevel, request.toLevel),
      ids = request.user.history.likedQuestProposalIds.flatten))))
  }
  
  def getVIPQuests(request: GetVIPQuestsRequest): ApiResult[GetVIPQuestsResult] = handleDbException {
    OkApiResult(Some(GetVIPQuestsResult(db.quest.allWithParams(
      status = Some(request.status.toString),
      levels = Some(request.fromLevel, request.toLevel),
      vip = Some(true),
      themeIds = request.themeIds))))
  }

  // TODO: test me to pass correct param to db.
  // TODO: pass to API levels in form of Option(tuple).
  def getAllQuests(request: GetAllQuestsRequest): ApiResult[GetAllQuestsResult] = handleDbException {
    Logger.trace("getAllQuests - " + request.toString);
    OkApiResult(Some(GetAllQuestsResult(db.quest.allWithParams(
      status = Some(request.status.toString),
      levels = Some(request.fromLevel, request.toLevel),
      themeIds = request.themeIds))))
  }
  
}


