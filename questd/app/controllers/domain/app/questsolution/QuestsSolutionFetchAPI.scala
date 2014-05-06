package controllers.domain.app.questsolution

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import play.Logger

case class GetFriendsSolutionRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None)
case class GetFriendsSolutionResult(quests: Iterator[QuestSolution])

case class GetShortlistSolutionRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None)
case class GetShortlistSolutionResult(quests: Iterator[QuestSolution])

case class GetSolutionsForLikedQuestsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None)
case class GetSolutionsForLikedQuestsResult(quests: Iterator[QuestSolution])

case class GetVIPSolutionsRequest(user: User, status: QuestStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String])
case class GetVIPSolutionsResult(quests: Iterator[QuestSolution])

case class GetAllSolutionsRequest(status: QuestStatus.Value, levels: Option[(Int, Int)] = None, themeIds: List[String] = List())
case class GetAllSolutionsResult(quests: Iterator[QuestSolution])


private[domain] trait QuestsSolutionFetchAPI { this: DBAccessor =>

  def getFriendsSolutions(request: GetFriendsSolutionRequest): ApiResult[GetFriendsSolutionResult] = handleDbException {
    OkApiResult(Some(GetFriendsSolutionResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      userIds = request.user.friends.filter(_.status == FriendshipStatus.Accepted.toString).map(_.friendId),
      levels = request.levels))))
  }

  def getShortlistSolutions(request: GetShortlistSolutionRequest): ApiResult[GetShortlistSolutionResult] = handleDbException {
    OkApiResult(Some(GetShortlistSolutionResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      userIds = request.user.shortlist,
      levels = request.levels))))
  }

  def getSolutionsForLikedQuests(request: GetSolutionsForLikedQuestsRequest): ApiResult[GetSolutionsForLikedQuestsResult] = handleDbException {
    OkApiResult(Some(GetSolutionsForLikedQuestsResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      levels = request.levels,
      questIds = request.user.history.likedQuestProposalIds.flatten))))
  }

  def getVIPSolutions(request: GetVIPSolutionsRequest): ApiResult[GetVIPSolutionsResult] = handleDbException {
    
    // TODO: use theme ids here as well.
    // request.themeIds
    
    OkApiResult(Some(GetVIPSolutionsResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      levels = request.levels,
      vip = Some(true)))))
  }
  
  def getAllSolutions(request: GetAllSolutionsRequest): ApiResult[GetAllSolutionsResult] = handleDbException {
    Logger.trace("getAllSolutions - " + request.toString)

    // TODO: use theme ids here as well.
    // request.themeIds
    
    OkApiResult(Some(GetAllSolutionsResult(db.solution.allWithParams(
      status = Some(request.status.toString),
      levels = request.levels))))
  }
}


