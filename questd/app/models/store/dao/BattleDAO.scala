package models.store.dao

import models.domain.battle.{BattleStatus, Battle}

trait BattleDAO extends BaseDAO[Battle] {

  /**
   * Get battles what meets following optional parameters.
   */
  def allWithParams(
    status: List[BattleStatus.Value] = List.empty,
    authorIds: List[String] = List.empty,
    authorIdsExclude: List[String] = List.empty,
    solutionIds: List[String] = List.empty,
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List.empty,
    idsExclude: List[String] = List.empty,
    cultureId: Option[String] = None): Iterator[Battle]

  /**
   * Update status of the battle
   * @param id Id of the battle.
   * @param newStatus New status of the battle
   * @return Updated battle.
   */
  def updateStatus(
    id: String,
    newStatus: BattleStatus.Value,
    setWinnerSolutionIds: List[String] = List.empty): Option[Battle]

  /**
   * Updates voting points of battle.
   *
   * @param id id of battle to vote for.
   * @param solutionId Solution we vote for.
   * @param randomPointsChange Random votes.
   * @param friendsPointsChange Friend votes.
   */
  def updatePoints(
    id: String,
    solutionId: String,
    randomPointsChange: Int,
    friendsPointsChange: Int): Option[Battle]

  /**
   * Replaces culture ids of battles with old one.
   * @param oldCultureId Replace thi culture id
   * @param newCultureId With this culture id.
   */
  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit

}

