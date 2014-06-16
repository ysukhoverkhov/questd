package controllers.domain.admin

import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._

case class AllQuestsRequest()
case class AllQuestsResult(quests: Iterator[Quest])

case class GetQuestAdminRequest(id: String)
case class GetQuestAdminResult(quest: Option[Quest])

case class UpdateQuestAdminRequest(
  id: String,
  status: String,
  level: Int,
  difficulty: String,
  duration: String)
case class UpdateQuestAdminResult()

private[domain] trait QuestsAdminAPI { this: DBAccessor =>

  /**
   * List all users
   */
  def allQuests(request: AllQuestsRequest): ApiResult[AllQuestsResult] = handleDbException {
    Logger.debug("Admin request for all Quests.")

    OkApiResult(Some(AllQuestsResult(db.quest.all)))
  }

  /**
   * Get quest by its id.
   */
  def getQuestAdmin(request: GetQuestAdminRequest): ApiResult[GetQuestAdminResult] = handleDbException {
    Logger.debug("Admin request for geting a quest.")

    OkApiResult(Some(GetQuestAdminResult(db.quest.readById(request.id))))
  }

  /**
   * Create new quest
   */
  def createQuestAdmin(request: CreateThemeRequest): ApiResult[CreateThemeResult] = handleDbException {
    import models.domain.base.ID

    Logger.debug("Admin request for create new quest.")

    db.theme.create(request.theme.copy(id = ID.generateUUID()))

    OkApiResult(Some(CreateThemeResult()))
  }

  /**
   * Update new theme
   */
  def updateQuestAdmin(request: UpdateQuestAdminRequest): ApiResult[UpdateQuestAdminResult] = handleDbException {
    Logger.debug("Admin request for update a quest " + request.id)

    db.quest.readById(request.id) match {
      case Some(q) => {
        db.quest.update(
          q.copy(
            status = QuestStatus.withName(request.status).toString,
            info = q.info.copy(
              level = request.level,
              difficulty = QuestDifficulty.withName(request.difficulty).toString,
              duration = QuestDuration.withName(request.duration).toString)))
      }

      case _ =>
    }

    OkApiResult(Some(UpdateQuestAdminResult()))
  }

}

