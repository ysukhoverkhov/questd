package controllers.domain.admin

import components.DBAccessor
import controllers.domain._
import controllers.domain.helpers._
import models.domain.quest.{Quest, QuestStatus}
import play.Logger

case class AllQuestsRequest()
case class AllQuestsResult(quests: Iterator[Quest])

case class GetQuestAdminRequest(id: String)
case class GetQuestAdminResult(quest: Option[Quest])

case class UpdateQuestAdminRequest(
  id: String,
  status: String,
  level: Int,
  description: String,
  points: Int,
  cheating: Int,
  votersCount: Int)
case class UpdateQuestAdminResult()

private[domain] trait QuestsAdminAPI { this: DBAccessor =>

  /**
   * List all users
   */
  def allQuests(request: AllQuestsRequest): ApiResult[AllQuestsResult] = handleDbException {
    Logger.debug("Admin request for all Quests.")

    OkApiResult(AllQuestsResult(db.quest.all))
  }

  /**
   * Get quest by its id.
   */
  def getQuestAdmin(request: GetQuestAdminRequest): ApiResult[GetQuestAdminResult] = handleDbException {
    Logger.debug("Admin request for geting a quest.")

    OkApiResult(GetQuestAdminResult(db.quest.readById(request.id)))
  }

  /**
   * Update a quest
   */
  def updateQuestAdmin(request: UpdateQuestAdminRequest): ApiResult[UpdateQuestAdminResult] = handleDbException {
    Logger.debug("Admin request for update a quest " + request.id)

    db.quest.readById(request.id) match {
      case Some(q) =>
        db.quest.update(
          q.copy(
            status = QuestStatus.withName(request.status),
            info = q.info.copy(
              level = request.level,
              content = q.info.content.copy(
                description = request.description)),
            rating = q.rating.copy(
              timelinePoints = request.points,
              cheating = request.cheating,
              votersCount = request.votersCount)))

      case _ =>
    }

    OkApiResult(UpdateQuestAdminResult())
  }

}

