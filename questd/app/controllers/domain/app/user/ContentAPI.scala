package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import models.domain.view._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import play.Logger
import controllers.domain.app.protocol.ProfileModificationResult._

case class GetQuestRequest(user: User, questId: String)
case class GetQuestResult(
  allowed: ProfileModificationResult,
  quest: Option[QuestInfo] = None,
  theme: Option[Theme] = None)

case class GetSolutionRequest(user: User, solutionId: String)
case class GetSolutionResult(
  allowed: ProfileModificationResult,
  mySolution: Option[QuestSolutionInfo] = None,
  myRating: Option[QuestSolutionRating] = None,
  rivalSolution: Option[QuestSolutionInfo] = None,
  rivalRating: Option[QuestSolutionRating] = None,
  rivalProfile: Option[PublicProfileWithID] = None,
  quest: Option[QuestInfo] = None)

case class GetPublicProfileRequest(user: User, userId: String)
case class GetPublicProfileResult(
  allowed: ProfileModificationResult,
  publicProfile: Option[PublicProfile])

case class GetOwnSolutionsRequest(
  user: User,
  status: Option[QuestSolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetOwnSolutionsResult(
  allowed: ProfileModificationResult,
  solutions: List[QuestSolutionInfoWithID],
  pageSize: Int,
  hasMore: Boolean)

case class GetOwnQuestsRequest(
  user: User,
  status: Option[QuestStatus.Value],
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
  status: Option[QuestSolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetSolutionsForQuestResult(
  allowed: ProfileModificationResult,
  solutions: List[QuestSolutionListInfo],
  pageSize: Int,
  hasMore: Boolean)

case class GetSolutionsForUserRequest(
  user: User,
  userId: String,
  status: Option[QuestSolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetSolutionsForUserResult(
  allowed: ProfileModificationResult,
  solutions: List[QuestSolutionListInfo],
  pageSize: Int,
  hasMore: Boolean)

case class GetQuestsForUserRequest(
  user: User,
  userId: String,
  status: Option[QuestStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetQuestsForUserResult(
  allowed: ProfileModificationResult,
  quests: List[Quest],
  pageSize: Int,
  hasMore: Boolean)

private[domain] trait ContentAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get quest by its id
   */
  def getQuest(request: GetQuestRequest): ApiResult[GetQuestResult] = handleDbException {
    import request._

    db.quest.readById(questId) match {
      case Some(q) => {
        db.theme.readById(q.info.themeId) match {
          case Some(t) => {
            OkApiResult(Some(GetQuestResult(OK, Some(q.info), Some(t))))
          }

          case None => {
            Logger.error("API - getQuest. Theme is missing for id = " + q.info.themeId)
            InternalErrorApiResult()
          }
        }
      }

      case None => {
        Logger.error("API - getQuest. Quest is missing for id = " + questId)
        InternalErrorApiResult()
      }
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

      val quest = db.quest.readById(s.info.questId).map(q => q.info)
      val rivalSolution = s.rivalSolutionId.flatMap(id => db.solution.readById(id))
      val rivalSolutionInfo = rivalSolution.map(rs => rs.info)
      val rivalRating = rivalSolution.map(rs => rs.rating)
      val rivalProfile = rivalSolution.flatMap(rs => db.user.readById(rs.userId)).flatMap(ru => Some(PublicProfileWithID(ru.id, ru.profile.publicProfile)))

      OkApiResult(Some(GetSolutionResult(
        allowed = OK,
        mySolution = Some(s.info),
        myRating = Some(s.rating),
        rivalSolution = rivalSolutionInfo,
        rivalRating = rivalRating,
        rivalProfile = rivalProfile,
        quest = quest)))
    }
  }

  /**
   * Get public profile
   */
  def getPublicProfile(request: GetPublicProfileRequest): ApiResult[GetPublicProfileResult] = handleDbException {

    getUser(UserRequest(userId = Some(request.userId))) map { r =>
      OkApiResult(Some(GetPublicProfileResult(
        allowed = OK,
        publicProfile = Some(r.user.profile.publicProfile))))
    }
  }

  /**
   * Get own solutions.
   */
  def getOwnSolutions(request: GetOwnSolutionsRequest): ApiResult[GetOwnSolutionsResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForUser = db.solution.allWithParams(
      status = request.status.map(_.toString),
      userIds = List(request.user.id),
      skip = pageNumber * pageSize)

    OkApiResult(Some(GetOwnSolutionsResult(
      allowed = OK,
      solutions = solutionsForUser.take(pageSize).toList.map(s => QuestSolutionInfoWithID(s.id, s.info)),
      pageSize,
      solutionsForUser.hasNext)))
  }

  /**
   * Get own quests.
   */
  def getOwnQuests(request: GetOwnQuestsRequest): ApiResult[GetOwnQuestsResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val questsForUser = db.quest.allWithParams(
      status = request.status.map(_.toString),
      userIds = List(request.user.id),
      skip = pageNumber * pageSize)

    OkApiResult(Some(GetOwnQuestsResult(
      allowed = OK,
      quests = questsForUser.take(pageSize).toList.map(q => QuestInfoWithID(q.id, q.info)),
      pageSize,
      questsForUser.hasNext)))
  }

  /**
   * Get solutions for a quest.
   */
  def getSolutionsForQuest(request: GetSolutionsForQuestRequest): ApiResult[GetSolutionsForQuestResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForQuest = db.solution.allWithParams(
      status = request.status.map(_.toString),
      questIds = List(request.questId),
      skip = pageNumber * pageSize)

    val solutions = solutionsForQuest.take(pageSize).toList.map(s => {
      QuestSolutionListInfo(
        solution = QuestSolutionInfoWithID(s.id, s.info),
        quest = None,
        author = db.user.readById(s.userId).map(us => PublicProfileWithID(us.id, us.profile.publicProfile)))
    })

    OkApiResult(Some(GetSolutionsForQuestResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForQuest.hasNext)))
  }

  /**
   * Get all solutions for a user.
   */
  def getSolutionsForUser(request: GetSolutionsForUserRequest): ApiResult[GetSolutionsForUserResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForUser = db.solution.allWithParams(
      status = request.status.map(_.toString),
      userIds = List(request.userId),
      skip = pageNumber * pageSize)

    val solutions = solutionsForUser.take(pageSize).toList.map(s => {
      QuestSolutionListInfo(
        solution = QuestSolutionInfoWithID(s.id, s.info),
        quest = db.quest.readById(s.info.questId).map(qu => QuestInfoWithID(qu.id, qu.info)),
        author = None)
    })

    OkApiResult(Some(GetSolutionsForUserResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForUser.hasNext)))
  }

  /**
   * Get all quests for a user.
   */
  def getQuestsForUser(request: GetQuestsForUserRequest): ApiResult[GetQuestsForUserResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val questsForUser = db.quest.allWithParams(
      request.status.map(_.toString),
      List(request.userId),
      skip = pageNumber * pageSize)

    OkApiResult(Some(GetQuestsForUserResult(
      allowed = OK,
      quests = questsForUser.take(pageSize).toList,
      pageSize,
      questsForUser.hasNext)))
  }

  /**
   * Make page of correct size correcting client's request.
   */
  private def adjustedPageSize(pageSize: Int): Int = {
    val maxPageSize = 50
    val defaultPageSize = 10

    if (pageSize <= 0)
      defaultPageSize
    else if (pageSize > maxPageSize)
      maxPageSize
    else
      pageSize
  }

  /**
   * Make page number of correct number correcting client's request.
   */
  private def adjustedPageNumber(pageNumber: Int): Int = {
    if (pageNumber < 0)
      0
    else
      pageNumber
  }

}

