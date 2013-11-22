package controllers.domain.admin

import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._

case class AllQuestsResult(quests: List[Quest])

private [domain] trait QuestsAdminAPI { this: DBAccessor => 


  /**
   * List all Quests
   */
  def allQuests: ApiResult[AllQuestsResult] = handleDbException {
    Logger.info("Admin request for all quests. THIS SHOULD NOT BE CALLED IN PRODUCTION SINCE IT'S VERY SLOW!!!!!!")

    OkApiResult(Some(AllQuestsResult(List() ++ db.quest.all)))
  }

}


