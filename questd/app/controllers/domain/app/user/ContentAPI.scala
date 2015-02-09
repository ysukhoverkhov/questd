package controllers.domain.app.user

import components._
import controllers.domain.{DomainAPIComponent, _}
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain._
import models.domain.view._

case class GetQuestRequest(user: User, questId: String)
case class GetQuestResult(
  allowed: ProfileModificationResult,
  quest: Option[QuestInfo] = None,
  theme: Option[ThemeInfo] = None)

case class GetSolutionRequest(user: User, solutionId: String)
case class GetSolutionResult(
  allowed: ProfileModificationResult,
  mySolution: Option[QuestSolutionInfoWithID] = None,
  myRating: Option[QuestSolutionRating] = None,
  rivalSolution: Option[QuestSolutionInfoWithID] = None,
  rivalRating: Option[QuestSolutionRating] = None,
  rivalProfile: Option[PublicProfileWithID] = None,
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
  status: List[QuestSolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetOwnSolutionsResult(
  allowed: ProfileModificationResult,
  solutions: List[QuestSolutionListInfo],
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
  status: List[QuestSolutionStatus.Value],
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
  status: List[QuestSolutionStatus.Value],
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
      db.theme.readById(quest.info.themeId) ifSome { theme =>
        OkApiResult(GetQuestResult(OK, Some(quest.info), Some(theme.info)))
      }
    }
  }

  /**
   * Get solution by its id
   */
  def getSolution(request: GetSolutionRequest): ApiResult[GetSolutionResult] = handleDbException {
    import request._

    db.solution.readById(solutionId).fold[ApiResult[GetSolutionResult]] {
      InternalErrorApiResult(s"API - getSolution. Unable to find solution in db with id = $solutionId")
    } { s =>

      val quest = db.quest.readById(s.info.questId)
      val questInfo = quest.map(q => QuestInfoWithID(q.id, q.info))
      val questAuthor = quest.flatMap(q => db.user.readById(q.info.authorId).map (u => PublicProfileWithID(u.id, u.profile.publicProfile)))
      val rivalSolution = s.rivalSolutionId.flatMap(id => db.solution.readById(id))
      val rivalSolutionInfo = rivalSolution.map(rs => QuestSolutionInfoWithID(rs.id, rs.info))
      val rivalRating = rivalSolution.map(rs => rs.rating)
      val rivalProfile = rivalSolution.flatMap(rs => db.user.readById(rs.info.authorId)).flatMap(ru => Some(PublicProfileWithID(ru.id, ru.profile.publicProfile)))

      OkApiResult(GetSolutionResult(
        allowed = OK,
        mySolution = Some(QuestSolutionInfoWithID(s.id, s.info)),
        myRating = Some(s.rating),
        rivalSolution = rivalSolutionInfo,
        rivalRating = rivalRating,
        rivalProfile = rivalProfile,
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
      status = request.status.map(_.toString),
      authorIds = List(request.user.id),
      skip = pageNumber * pageSize)

    val solutions = solutionsForUser.take(pageSize).toList.map(s => {
      QuestSolutionListInfo(
        solution = QuestSolutionInfoWithID(s.id, s.info),
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
      status = request.status.map(_.toString),
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
      status = request.status.filter(Set(QuestSolutionStatus.Won, QuestSolutionStatus.Lost).contains).map(_.toString),
      questIds = List(request.questId),
      skip = pageNumber * pageSize)

    val solutions = solutionsForQuest.take(pageSize).toList.map(s => {
      QuestSolutionListInfo(
        solution = QuestSolutionInfoWithID(s.id, s.info),
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
      status = request.status.filter(Set(QuestSolutionStatus.Won, QuestSolutionStatus.Lost).contains).map(_.toString),
      authorIds = List(request.userId),
      skip = pageNumber * pageSize)

    val solutions = solutionsForUser.take(pageSize).toList.map(s => {
      QuestSolutionListInfo(
        solution = QuestSolutionInfoWithID(s.id, s.info),
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
      request.status.filter(Set(QuestStatus.InRotation).contains).map(_.toString),
      List(request.userId),
      skip = pageNumber * pageSize)

    OkApiResult(GetQuestsForUserResult(
      allowed = OK,
      quests = questsForUser.take(pageSize).toList.map(q => QuestInfoWithID(q.id, q.info)),
      pageSize,
      questsForUser.hasNext))
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

