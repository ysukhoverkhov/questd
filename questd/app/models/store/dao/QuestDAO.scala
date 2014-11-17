package models.store.dao

import models.domain._

trait QuestDAO extends BaseDAO[Quest] {

  /**
   * Count quests with given status.
   */
  def countWithStatus(status: String): Long

  /**
   * All with status and users.
   *
   * @param ids list of identifiers chose quest from.
   */
  def allWithParams(
    status: List[QuestStatus.Value] = List(),
    authorIds: List[String] = List(),
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List(),
    cultureId: Option[String] = None): Iterator[Quest]

  /**
   * Update quest's points.
   */
  def updatePoints(
    id: String,
    pointsChange: Int,
    votersCountChange: Int = 0,
    cheatingChange: Int = 0,
    spamChange: Int = 0,
    pornChange: Int = 0): Option[Quest]

  def updateStatus(id: String, newStatus: QuestStatus.Value): Option[Quest]

  def updateInfo(id: String, newLevel: Int): Option[Quest]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit
}

