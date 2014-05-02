package controllers.domain.app.quest

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._

// TODO: perhaps this all quests should be removed because we have other all quests bellow.
case class AllQuestsRequest(status: QuestStatus.Value, fromLevel: Int, toLevel: Int)
case class AllQuestsResult(quests: Iterator[Quest])

// TODO: this solution is in file dedicated to quests by mistake.
case class AllQuestSolutionsRequest(minLevel: Int, maxLevel: Int)
case class AllQuestSolutionsResult(quests: Iterator[QuestSolution])



case class GetFriendsQuestsRequest(user: User, status: QuestStatus.Value, fromLevel: Int, toLevel: Int)
case class GetFriendsQuestsResult(quests: Iterator[Quest])

case class GetShortlistQuestsRequest(user: User, status: QuestStatus.Value, fromLevel: Int, toLevel: Int)
case class GetShortlistQuestsResult(quests: Iterator[Quest])

case class GetVIPQuestsRequest(user: User, status: QuestStatus.Value, fromLevel: Int, toLevel: Int, themeIds: List[String])
case class GetVIPQuestsResult(quests: Iterator[Quest])

case class GetLikedQuestsRequest(user: User, status: QuestStatus.Value, fromLevel: Int, toLevel: Int)
case class GetLikedQuestsResult(quests: Iterator[Quest])

case class GetAllQuestsRequest(user: User, status: QuestStatus.Value, fromLevel: Int, toLevel: Int, themeIds: List[String])
case class GetAllQuestsResult(quests: Iterator[Quest])


private [domain] trait QuestsFetchAPI { this: DBAccessor => 

  /**
   * List all Quests with specified status.
   */
  def allQuestsWithStatus(request: AllQuestsRequest): ApiResult[AllQuestsResult] = handleDbException {
    OkApiResult(Some(AllQuestsResult(db.quest.allWithParams(
        status = Some(request.status.toString),
        levels = Some(request.fromLevel, request.toLevel)))))
  }

  /**
   * List all Quests solution s with OnVoting status.
   */
  def allQuestSolutionsOnVoting(request: AllQuestSolutionsRequest): ApiResult[AllQuestSolutionsResult] = handleDbException {
    OkApiResult(Some(AllQuestSolutionsResult(db.solution.allWithStatusAndLevels(QuestSolutionStatus.OnVoting.toString, request.minLevel, request.maxLevel))))
  }
  
  
  def getFriendsQuests(request: GetFriendsQuestsRequest): ApiResult[GetFriendsQuestsResult] = handleDbException {
    OkApiResult(Some(GetFriendsQuestsResult(db.quest.allWithParams(
      Some(QuestStatus.InRotation.toString),
      request.user.friends.filter(_.status == FriendshipStatus.Accepted.toString).map(_.friendId),
      Some(request.fromLevel, request.toLevel)))))
  }

  def getShortlistQuests(request: GetShortlistQuestsRequest): ApiResult[GetShortlistQuestsResult] = handleDbException {
    OkApiResult(Some(GetShortlistQuestsResult(db.quest.allWithParams(
      Some(QuestStatus.InRotation.toString),
      request.user.shortlist,
      Some(request.fromLevel, request.toLevel)))))
  }
  
  def getLikedQuests(request: GetLikedQuestsRequest): ApiResult[GetLikedQuestsResult] = handleDbException {
    OkApiResult(Some(GetLikedQuestsResult(db.quest.allWithParams(
      status = Some(QuestStatus.InRotation.toString),
      levels = Some(request.fromLevel, request.toLevel),
      ids = request.user.history.likedQuestProposalIds.flatten))))
  }
  
  def getVIPQuests(request: GetVIPQuestsRequest): ApiResult[GetVIPQuestsResult] = handleDbException {
    OkApiResult(Some(GetVIPQuestsResult(db.quest.allWithParams(
      status = Some(QuestStatus.InRotation.toString),
      levels = Some(request.fromLevel, request.toLevel),
      vip = Some(true),
      themeIds = request.themeIds))))
  }

  def getAllQuests(request: GetAllQuestsRequest): ApiResult[GetAllQuestsResult] = handleDbException {
    OkApiResult(Some(GetAllQuestsResult(db.quest.allWithParams(
      status = Some(QuestStatus.InRotation.toString),
      levels = Some(request.fromLevel, request.toLevel),
      themeIds = request.themeIds))))
  }
  
}


