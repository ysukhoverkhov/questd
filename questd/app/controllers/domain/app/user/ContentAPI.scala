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
case class GetQuestResult(allowed: ProfileModificationResult, quest: Option[QuestInfo], theme: Option[Theme])

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

}

