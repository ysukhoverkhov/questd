package controllers.domain.app.battle

import components.DBAccessor
import controllers.domain.{OkApiResult, ApiResult}
import controllers.domain.helpers._
import models.domain.{BattleStatus, Battle}

case class GetAllBattlesRequest()
case class GetAllBattlesResult(battles: Iterator[Battle])

private[domain] trait BattleFetchAPI { this: DBAccessor =>

  def getAllBattles(request: GetAllBattlesRequest): ApiResult[GetAllBattlesResult] = handleDbException {
    OkApiResult(GetAllBattlesResult(db.battle.allWithParams(
      status = List(BattleStatus.Fighting))))
  }
}
