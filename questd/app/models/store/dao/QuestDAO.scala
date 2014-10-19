package models.store.dao

import models.domain._

trait QuestDAO extends BaseDAO[Quest] {

  /**
   * Count quests with given status.
   */
  def countWithStatus(status: String): Long

  /**
   * All quests with status for theme id ordered by points.
   */
  def allWithStatusAndThemeByPoints(status: String, themeId: String): Iterator[Quest]

  /**
   * All with status and users.
   *
   * @param ids list of identifiers chose quest from.
   */
  def allWithParams(
    status: List[String] = List(),
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
    votersCountChange: Int,
    cheatingChange: Int = 0,
    spamChange: Int = 0,
    pornChange: Int = 0): Option[Quest]

  def updateStatus(id: String, newStatus: String): Option[Quest]

  def updateInfo(id: String, newLevel: Int): Option[Quest]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit
}

