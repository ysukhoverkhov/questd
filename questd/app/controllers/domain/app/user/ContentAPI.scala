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

private[domain] trait ContentAPI { this: DBAccessor =>

  /**
   * Get quest by its id
   */
  def getQuest(request: GetQuestRequest): ApiResult[GetQuestResult] = handleDbException {
    import request._

    db.quest.readByID(questId) match {
      case Some(q) => {
        db.theme.readByID(q.themeID) match {
          case Some(t) => {
            OkApiResult(Some(GetQuestResult(OK, Some(q.info), Some(t))))
          }

          case None => {
            Logger.error("API - getQuest. Theme is missing for id = " + q.themeID)
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

  // TODO set rival in solution

  /**
   * Get solution by its id
   */
  def getSolution(request: GetSolutionRequest): ApiResult[GetSolutionResult] = handleDbException {
    import request._

    db.solution.readByID(solutionId).fold[ApiResult[GetSolutionResult]] {
      Logger.error("API - getSolution. Unable to find solution in db with id = " + solutionId)
      InternalErrorApiResult()
    } { s =>

      val quest = db.quest.readByID(s.questID).map(q => q.info)
      val rivalSolution = s.rivalSolutionId.flatMap(id => db.solution.readByID(id))
      val rivalSolutionInfo = rivalSolution.map(rs => rs.info)
      val rivalRating = rivalSolution.map(rs => rs.rating)
      val rivalProfile = rivalSolution.flatMap(rs => db.user.readByID(rs.userID)).map(ru => ru.profile)  

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
}

