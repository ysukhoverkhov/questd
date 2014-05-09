package controllers.domain.app.questsolution

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import play.Logger

case class GetFriendsSolutionsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None)
case class GetFriendsSolutionsResult(quests: Iterator[QuestSolution])

case class GetShortlistSolutionsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None)
case class GetShortlistSolutionsResult(quests: Iterator[QuestSolution])

case class GetSolutionsForLikedQuestsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None)
case class GetSolutionsForLikedQuestsResult(quests: Iterator[QuestSolution])

case class GetVIPSolutionsRequest(user: User, status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String])
case class GetVIPSolutionsResult(quests: Iterator[QuestSolution])

case class GetAllSolutionsRequest(status: QuestSolutionStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String] = List())
case class GetAllSolutionsResult(quests: Iterator[QuestSolution])


private[domain] trait QuestsSolutionFetchAPI { this: DBAccessor =>

  def getFriendsSolutions(request: GetFriendsSolutionsRequest): ApiResult[GetFriendsSolutionsResult] = handleDbException {
    OkApiResult(Some(GetFriendsSolutionsResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      userIds = request.user.friends.filter(_.status == FriendshipStatus.Accepted.toString).map(_.friendId),
      levels = request.levels))))
  }

  def getShortlistSolutions(request: GetShortlistSolutionsRequest): ApiResult[GetShortlistSolutionsResult] = handleDbException {
    OkApiResult(Some(GetShortlistSolutionsResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      userIds = request.user.shortlist,
      levels = request.levels))))
  }

  def getSolutionsForLikedQuests(request: GetSolutionsForLikedQuestsRequest): ApiResult[GetSolutionsForLikedQuestsResult] = handleDbException {
    import models.store.mongo.helpers._
    
    OkApiResult(Some(GetSolutionsForLikedQuestsResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      levels = request.levels,
      questIds = request.user.history.likedQuestProposalIds.mongoFlatten))))
  }

  def getVIPSolutions(request: GetVIPSolutionsRequest): ApiResult[GetVIPSolutionsResult] = handleDbException {
    OkApiResult(Some(GetVIPSolutionsResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      levels = request.levels,
      vip = Some(true),
      themeIds = request.themeIds))))
  }
  
  def getAllSolutions(request: GetAllSolutionsRequest): ApiResult[GetAllSolutionsResult] = handleDbException {
    Logger.trace("getAllSolutions - " + request.toString)

    OkApiResult(Some(GetAllSolutionsResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      levels = request.levels,
      themeIds = request.themeIds))))
  }
}


