package models.store.dao

import models.domain._

trait BattleDAO extends BaseDAO[Battle] {

  /**
   * Get battles what meets following optional parameters.
   */
  // TODO: implement me.
//  def allWithParams(
//    status: List[SolutionStatus.Value] = List(),
//    authorIds: List[String] = List(),
//    levels: Option[(Int, Int)] = None,
//    skip: Int = 0,
//    vip: Option[Boolean] = None,
//    ids: List[String] = List(),
//    questIds: List[String] = List(),
//    themeIds: List[String] = List(),
//    cultureId: Option[String] = None): Iterator[Solution]

  def updateStatus(
    id: String,
    newStatus: BattleStatus.Value): Option[Battle]

}

