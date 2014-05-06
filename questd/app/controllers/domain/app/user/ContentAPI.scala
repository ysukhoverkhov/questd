package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
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
  rivalProfile: Option[Profile] = None,
  quest: Option[QuestInfo] = None)

  
case class GetPublicProfileRequest(user: User, userId: String)
case class GetPublicProfileResult(
  allowed: ProfileModificationResult,
  publicProfile: Option[PublicProfile])

  
case class GetSolutionsForQuestRequest(
  user: User,
  questId: String,
  status: Option[QuestSolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetSolutionsForQuestResult(
  allowed: ProfileModificationResult,
  solutions: List[QuestSolution],
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
  solutions: List[QuestSolution],
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

      val quest = db.quest.readById(s.questId).map(q => q.info)
      val rivalSolution = s.rivalSolutionId.flatMap(id => db.solution.readById(id))
      val rivalSolutionInfo = rivalSolution.map(rs => rs.info)
      val rivalRating = rivalSolution.map(rs => rs.rating)
      val rivalProfile = rivalSolution.flatMap(rs => db.user.readById(rs.userId)).map(ru => ru.profile)

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
   * Get solutions for a quest.
   */
  // TODO: test me.
  // TODO: rewrite me with modern db api
  def getSolutionsForQuest(request: GetSolutionsForQuestRequest): ApiResult[GetSolutionsForQuestResult] = handleDbException {
    val pageSize = if (request.pageSize > 50) 50 else request.pageSize
    
    val solutionsForQuest = db.solution.allWithStatusAndQuest(
        request.status.map(_.toString), 
        request.questId,
        request.pageNumber * pageSize)

    OkApiResult(Some(GetSolutionsForQuestResult(
      allowed = OK,
      solutions = solutionsForQuest.take(pageSize).toList,
      pageSize,
      solutionsForQuest.hasNext)))
  }

  /**
   * Get all solutions for a user.
   */
  // TODO: test me.
  // TODO: rewrite me with modern db api
  def getSolutionsForUser(request: GetSolutionsForUserRequest): ApiResult[GetSolutionsForUserResult] = handleDbException {
    val pageSize = if (request.pageSize > 50) 50 else request.pageSize
    
    val solutionsForUser = db.solution.allWithStatusAndUser(
        request.status.map(_.toString), 
        request.userId,
        request.pageNumber * pageSize)

    OkApiResult(Some(GetSolutionsForUserResult(
      allowed = OK,
      solutions = solutionsForUser.take(pageSize).toList,
      pageSize,
      solutionsForUser.hasNext)))
  }

  /**
   * Get all quests for a user.
   */
  def getQuestsForUser(request: GetQuestsForUserRequest): ApiResult[GetQuestsForUserResult] = handleDbException {
    val pageSize = if (request.pageSize > 50) 50 else request.pageSize
    
    val questsForUser = db.quest.allWithParams(
        request.status.map(_.toString), 
        List(request.userId),
        skip = request.pageNumber * pageSize)

    OkApiResult(Some(GetQuestsForUserResult(
      allowed = OK,
      quests = questsForUser.take(pageSize).toList,
      pageSize,
      questsForUser.hasNext)))
  }
}

