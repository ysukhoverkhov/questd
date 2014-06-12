package controllers.domain.admin

import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._

case class AllQuestsRequest()
case class AllQuestsResult(quests: Iterator[Quest])

private[domain] trait QuestsAdminAPI { this: DBAccessor =>

  /**
   * List all users
   */
  def allQuests(request: AllQuestsRequest): ApiResult[AllQuestsResult] = handleDbException {
    Logger.debug("Admin request for all Quests.")

    OkApiResult(Some(AllQuestsResult(db.quest.all)))
  }

}

