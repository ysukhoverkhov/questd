package controllers.domain.app.user

import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import models.domain.view._
import play.Logger
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers.PagerHelper._

case class GetQuestRequest(user: User, questId: String)
case class GetQuestResult(
  allowed: ProfileModificationResult,
  quest: Option[QuestInfo] = None)

case class GetSolutionRequest(user: User, solutionId: String)
case class GetSolutionResult(
  allowed: ProfileModificationResult,
  mySolution: Option[SolutionInfoWithID] = None,
  myRating: Option[SolutionRating] = None,
  quest: Option[QuestInfoWithID] = None,
  questAuthor: Option[PublicProfileWithID] = None)

case class GetPublicProfilesRequest(
  user: User,
  userIds: List[String])
case class GetPublicProfilesResult(
  allowed: ProfileModificationResult,
  publicProfiles: List[PublicProfileWithID])

case class GetOwnSolutionsRequest(
  user: User,
  status: List[SolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetOwnSolutionsResult(
  allowed: ProfileModificationResult,
  solutions: List[SolutionListInfo],
  pageSize: Int,
  hasMore: Boolean)

case class GetOwnQuestsRequest(
  user: User,
  status: List[QuestStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetOwnQuestsResult(
  allowed: ProfileModificationResult,
  quests: List[QuestInfoWithID],
  pageSize: Int,
  hasMore: Boolean)

case class GetSolutionsForQuestRequest(
  user: User,
  questId: String,
  status: List[SolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetSolutionsForQuestResult(
  allowed: ProfileModificationResult,
  solutions: List[SolutionListInfo],
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
  solutions: List[SolutionListInfo],
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
  quests: List[QuestInfoWithID],
  pageSize: Int,
  hasMore: Boolean)

private[domain] trait ContentAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get quest by its id
   */
  def getQuest(request: GetQuestRequest): ApiResult[GetQuestResult] = handleDbException {
    import request._

    db.quest.readById(questId) ifSome { quest =>
      OkApiResult(GetQuestResult(OK, Some(quest.info)))
    }
  }

  /**
   * Get solution by its id
   */
  def getSolution(request: GetSolutionRequest): ApiResult[GetSolutionResult] = handleDbException {
    import request._

    db.solution.readById(solutionId).fold[ApiResult[GetSolutionResult]] {
      Logger.error("API - getSolution. Unable to find solution in db with id = " + solutionId)
      InternalErrorApiResult()
    } { s =>

      val quest = db.quest.readById(s.info.questId)
      val questInfo = quest.map(q => QuestInfoWithID(q.id, q.info))
      val questAuthor = quest.flatMap(q => db.user.readById(q.info.authorId).map (u => PublicProfileWithID(u.id, u.profile.publicProfile)))

      OkApiResult(GetSolutionResult(
        allowed = OK,
        mySolution = Some(SolutionInfoWithID(s.id, s.info)),
        myRating = Some(s.rating),
        quest = questInfo,
        questAuthor = questAuthor))
    }
  }

  /**
   * Get public profile
   */
  def getPublicProfiles(request: GetPublicProfilesRequest): ApiResult[GetPublicProfilesResult] = handleDbException {
    val maxPageSize = 50

    OkApiResult(GetPublicProfilesResult(
      allowed = OK,
      publicProfiles = db.user.readSomeByIds(request.userIds.take(maxPageSize)).toList.map(u => PublicProfileWithID(u.id, u.profile.publicProfile))))
  }

  /**
   * Get own solutions.
   */
  def getOwnSolutions(request: GetOwnSolutionsRequest): ApiResult[GetOwnSolutionsResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForUser = db.solution.allWithParams(
      status = request.status,
      authorIds = List(request.user.id),
      skip = pageNumber * pageSize)

    val solutions = solutionsForUser.take(pageSize).toList.map(s => {
      SolutionListInfo(
        solution = SolutionInfoWithID(s.id, s.info),
        quest = db.quest.readById(s.info.questId).map(qu => QuestInfoWithID(qu.id, qu.info)),
        author = None)
    })

    OkApiResult(GetOwnSolutionsResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForUser.hasNext))
  }

  /**
   * Get own quests.
   */
  def getOwnQuests(request: GetOwnQuestsRequest): ApiResult[GetOwnQuestsResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val questsForUser = db.quest.allWithParams(
      status = request.status,
      authorIds = List(request.user.id),
      skip = pageNumber * pageSize)

    OkApiResult(GetOwnQuestsResult(
      allowed = OK,
      quests = questsForUser.take(pageSize).toList.map(q => QuestInfoWithID(q.id, q.info)),
      pageSize,
      questsForUser.hasNext))
  }

  /**
   * Get solutions for a quest.
   */
  def getSolutionsForQuest(request: GetSolutionsForQuestRequest): ApiResult[GetSolutionsForQuestResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForQuest = db.solution.allWithParams(
      status = request.status.filter(Set(SolutionStatus.Won, SolutionStatus.Lost).contains),
      questIds = List(request.questId),
      skip = pageNumber * pageSize)

    val solutions = solutionsForQuest.take(pageSize).toList.map(s => {
      SolutionListInfo(
        solution = SolutionInfoWithID(s.id, s.info),
        quest = None,
        author = db.user.readById(s.info.authorId).map(us => PublicProfileWithID(us.id, us.profile.publicProfile)))
    })

    OkApiResult(GetSolutionsForQuestResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForQuest.hasNext))
  }

  /**
   * Get all solutions for a user.
   */
  def getSolutionsForUser(request: GetSolutionsForUserRequest): ApiResult[GetSolutionsForUserResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForUser = db.solution.allWithParams(
      status = request.status.filter(Set(SolutionStatus.Won, SolutionStatus.Lost).contains),
      authorIds = List(request.userId),
      skip = pageNumber * pageSize)

    val solutions = solutionsForUser.take(pageSize).toList.map(s => {
      SolutionListInfo(
        solution = SolutionInfoWithID(s.id, s.info),
        quest = db.quest.readById(s.info.questId).map(qu => QuestInfoWithID(qu.id, qu.info)),
        author = None)
    })

    OkApiResult(GetSolutionsForUserResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForUser.hasNext))
  }

  /**
   * Get all quests for a user.
   */
  def getQuestsForUser(request: GetQuestsForUserRequest): ApiResult[GetQuestsForUserResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val questsForUser = db.quest.allWithParams(
      request.status.filter(Set(QuestStatus.InRotation).contains),
      List(request.userId),
      skip = pageNumber * pageSize)

    OkApiResult(GetQuestsForUserResult(
      allowed = OK,
      quests = questsForUser.take(pageSize).toList.map(q => QuestInfoWithID(q.id, q.info)),
      pageSize,
      questsForUser.hasNext))
  }

}

