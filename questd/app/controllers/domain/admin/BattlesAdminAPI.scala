package controllers.domain.admin

import components.DBAccessor
import controllers.domain._
import controllers.domain.helpers._
import models.domain._
import models.domain.battle.Battle
import play.Logger

case class AllBattlesRequest()
case class AllBattlesResult(battles: Iterator[Battle])

private[domain] trait BattlesAdminAPI { this: DBAccessor =>

  /**
   * List all Battles
   */
  def allBattles(request: AllBattlesRequest): ApiResult[AllBattlesResult] = handleDbException {
    Logger.debug("Admin request for all Battles.")

    OkApiResult(AllBattlesResult(db.battle.all))
  }

}

