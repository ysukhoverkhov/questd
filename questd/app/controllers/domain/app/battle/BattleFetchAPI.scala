package controllers.domain.app.battle

import components.DBAccessor
import controllers.domain.{OkApiResult, ApiResult}
import controllers.domain.helpers._
import models.domain.{User, BattleStatus, Battle}

case class GetAllBattlesInternalRequest()
case class GetAllBattlesInternalResult(battles: Iterator[Battle])

case class GetAllBattlesRequest(
  user: User,
  excludeIds: List[String] = List(),
  statuses: List[BattleStatus.Value] = List.empty,
  levels: Option[(Int, Int)] = None)
case class GetAllBattlesResult(battles: Iterator[Battle])

private[domain] trait BattleFetchAPI { this: DBAccessor =>

  /**
   * Get all battles in fighting state. used internally.
   * @param request The request.
   * @return
   */
  def getAllBattlesInternal(request: GetAllBattlesInternalRequest): ApiResult[GetAllBattlesInternalResult] = handleDbException {
    OkApiResult(GetAllBattlesInternalResult(db.battle.allWithParams(
      status = List(BattleStatus.Fighting))))
  }

  /**
   * Returns battles with params.
   * @param request The request.
   * @return
   */
  // TODO: take culture into account here.
  def getAllBattles(request: GetAllBattlesRequest): ApiResult[GetAllBattlesResult] = handleDbException {
    OkApiResult(GetAllBattlesResult(db.battle.allWithParams(
      status = request.statuses,
      levels = request.levels,
      idsExclude = request.excludeIds)))
  }

}
