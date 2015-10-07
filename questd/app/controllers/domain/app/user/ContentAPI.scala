package controllers.domain.app.user

import components._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import models.domain.battle.BattleStatus
import models.domain.quest.QuestStatus
import models.domain.solution.SolutionStatus
import models.domain.user.User
import models.view.{BattleView, ProfileView, QuestView, SolutionView}

case class GetQuestsRequest(user: User, questIds: List[String])
case class GetQuestsResult(
  allowed: ProfileModificationResult,
  quests: List[QuestView] = List.empty)

case class GetSolutionsRequest(user: User, solutionIds: List[String])
case class GetSolutionsResult(
  allowed: ProfileModificationResult,
  solutions: List[SolutionView] = List.empty)

case class GetBattlesRequest(user: User, battleIds: List[String])
case class GetBattlesResult(
  allowed: ProfileModificationResult,
  battles: List[BattleView] = List.empty)

case class GetPublicProfilesRequest(
  user: User,
  userIds: List[String])
case class GetPublicProfilesResult(
  allowed: ProfileModificationResult,
  publicProfiles: List[ProfileView])

case class GetSolutionsForQuestRequest(
  user: User,
  questId: String,
  status: List[SolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetSolutionsForQuestResult(
  allowed: ProfileModificationResult,
  solutions: List[SolutionView],
  pageSize: Int,
  hasMore: Boolean)

case class GetSolutionsForUserRequest(
  user: User,
  userId: String,
  status: List[SolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetSolutionsForUserResult(
  allowed: ProfileModificationResult,
  solutions: List[SolutionView],
  pageSize: Int,
  hasMore: Boolean)

case class GetQuestsForUserRequest(
  user: User,
  userId: String,
  status: List[QuestStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetQuestsForUserResult(
  allowed: ProfileModificationResult,
  quests: List[QuestView],
  pageSize: Int,
  hasMore: Boolean)

case class GetBattlesForUserRequest(
  user: User,
  userId: String,
  status: List[BattleStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetBattlesForUserResult(
  allowed: ProfileModificationResult,
  battles: List[BattleView],
  pageSize: Int,
  hasMore: Boolean)

case class GetBattlesForSolutionRequest(
  user: User,
  solutionId: String,
  status: List[BattleStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetBattlesForSolutionResult(
  allowed: ProfileModificationResult,
  battles: List[BattleView],
  pageSize: Int,
  hasMore: Boolean)

case class GetBattlesForQuestRequest(
  user: User,
  questId: String,
  status: List[BattleStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetBattlesForQuestResult(
  allowed: ProfileModificationResult,
  battles: List[BattleView],
  pageSize: Int,
  hasMore: Boolean)


private[domain] trait ContentAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get quest by its id
   */
  def getQuests(request: GetQuestsRequest): ApiResult[GetQuestsResult] = handleDbException {
    import request._
    val maxPageSize = adjustedPageSize(questIds.length)

    OkApiResult(GetQuestsResult(
      OK,
      db.quest.readManyByIds(questIds.take(maxPageSize)).map(QuestView(_, user)).toList))
  }

  /**
   * Get solution by its id
   */
  def getSolutions(request: GetSolutionsRequest): ApiResult[GetSolutionsResult] = handleDbException {
    import request._
    val maxPageSize = adjustedPageSize(solutionIds.length)

    OkApiResult(GetSolutionsResult(
      OK,
      db.solution.readManyByIds(solutionIds.take(maxPageSize)).map(SolutionView(_, user)).toList))
  }

  /**
   * Get battle by its id.
   * @param request The request.
   * @return
   */
  def getBattles(request: GetBattlesRequest): ApiResult[GetBattlesResult] = handleDbException {
    import request._
    val maxPageSize = adjustedPageSize(battleIds.length)

    OkApiResult(GetBattlesResult(
      OK,
      db.battle.readManyByIds(battleIds.take(maxPageSize)).map{ b =>
        BattleView(
          b.id,
          b.info,
          user.stats.votedBattles.get(b.id))
      }.toList))
  }

  /**
   * Get public profile
   */
  def getPublicProfiles(request: GetPublicProfilesRequest): ApiResult[GetPublicProfilesResult] = handleDbException {
    val maxPageSize = adjustedPageSize(request.userIds.length)

    OkApiResult(GetPublicProfilesResult(
      allowed = OK,
      publicProfiles = db.user.readManyByIds(request.userIds.take(maxPageSize)).toList.map(u => ProfileView(u.id, u.profile.publicProfile))))
  }

  /**
   * Get all quests for a user.
   */
  def getQuestsForUser(request: GetQuestsForUserRequest): ApiResult[GetQuestsForUserResult] = handleDbException {
    import request._
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val adjustedStatuses = if (request.userId == user.id) {
      status
    } else {
      List(QuestStatus.InRotation)//request.status.filter(Set(QuestStatus.InRotation).contains)
    }

    val questsForUser = db.quest.allWithParams(
      status = adjustedStatuses,
      authorIds = List(request.userId),
      skip = pageNumber * pageSize)

    OkApiResult(GetQuestsForUserResult(
      allowed = OK,
      quests = questsForUser.take(pageSize).toList.map(QuestView(_, user)),
      pageSize,
      questsForUser.hasNext))
  }

  /**
   * Get all solutions for a user.
   */
  def getSolutionsForUser(request: GetSolutionsForUserRequest): ApiResult[GetSolutionsForUserResult] = handleDbException {
    import request._
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val adjustedStatuses = if (request.userId == user.id) {
      status
    } else {
      List(SolutionStatus.InRotation) //request.status.filter(Set(SolutionStatus.InRotation).contains)
    }

    val solutionsForUser = db.solution.allWithParams(
      status = adjustedStatuses,
      authorIds = List(request.userId),
      skip = pageNumber * pageSize)

    val solutions = solutionsForUser.take(pageSize).toList.map(SolutionView(_, user))

    OkApiResult(GetSolutionsForUserResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForUser.hasNext))
  }

  /**
   * Get solutions for a quest.
   */
  def getSolutionsForQuest(request: GetSolutionsForQuestRequest): ApiResult[GetSolutionsForQuestResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForQuest = db.solution.allWithParams(
      status = List(SolutionStatus.InRotation), // request.status.filter(Set(SolutionStatus.InRotation).contains),
      questIds = List(request.questId),
      authorIdsExclude = request.user.banned,
      skip = pageNumber * pageSize)

    val solutions = solutionsForQuest.take(pageSize).toList.map(SolutionView(_, request.user))

    OkApiResult(GetSolutionsForQuestResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForQuest.hasNext))
  }

  /**
   * Get all battle for a user.
   */
  def getBattlesForUser(request: GetBattlesForUserRequest): ApiResult[GetBattlesForUserResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val battlesForUser = db.battle.allWithParams(
      status = request.status,
      authorIds = List(request.userId),
      authorIdsExclude = request.user.banned,
      skip = pageNumber * pageSize)

    val battles = battlesForUser.take(pageSize).toList.map( b => {
      BattleView(
        b.id,
        b.info,
        request.user.stats.votedBattles.get(b.id))
    })

    OkApiResult(GetBattlesForUserResult(
      allowed = OK,
      battles = battles,
      pageSize,
      battlesForUser.hasNext))
  }

  /**
   * Get battles for a solution.
   */
  def getBattlesForSolution(request: GetBattlesForSolutionRequest): ApiResult[GetBattlesForSolutionResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val battlesForSolution = db.battle.allWithParams(
      status = request.status,
      solutionIds = List(request.solutionId),
      authorIdsExclude = request.user.banned,
      skip = pageNumber * pageSize)

    val battles = battlesForSolution.take(pageSize).toList.map(b => {
      BattleView(
        b.id,
        b.info,
        request.user.stats.votedBattles.get(b.id))
    })

    OkApiResult(GetBattlesForSolutionResult(
      allowed = OK,
      battles = battles,
      pageSize,
      battlesForSolution.hasNext))
  }


  /**
   * Returns duels people made for the quest.
   */
  def getBattlesForQuest(request: GetBattlesForQuestRequest): ApiResult[GetBattlesForQuestResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val battlesForQuest = db.battle.allWithParams(
      status = request.status,
      questId = Some(request.questId),
      authorIdsExclude = request.user.banned,
      skip = pageNumber * pageSize)

    val battles = battlesForQuest.take(pageSize).toList.map(b => {
      BattleView(
        b.id,
        b.info,
        request.user.stats.votedBattles.get(b.id))
    })

    OkApiResult(GetBattlesForQuestResult(
      allowed = OK,
      battles = battles,
      pageSize,
      battlesForQuest.hasNext))
  }

}

