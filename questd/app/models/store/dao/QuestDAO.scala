package models.store.dao

import models.domain._

trait QuestDAO extends BaseDAO[Quest] {

  /**
   * Count quests with given status.
   */
  def countWithStatus(status: String): Long

  /**
   * Return iterator to quests with given status and levels.
   */
  def allWithStatusAndLevels(status: String, minLevel: Int, maxLevel: Int): Iterator[Quest]

  /**
   * All quests with status for theme id ordered by points.
   */
  def allWithStatusAndThemeByPoints(status: String, themeID: String): Iterator[Quest]

  /**
   * All with status and users.
   */
  def allWithStatusAndUsers(status: Option[String], userIds: List[String], skip: Int = 0): Iterator[Quest]

  /**
   * Update quest's points.
   */
  def updatePoints(
    id: String,
    pointsChange: Int,
    votersCountChange: Int,
    cheatingChange: Int = 0,
    spamChange: Int = 0,
    pornChange: Int = 0,

    easyChange: Int = 0,
    normalChange: Int = 0,
    hardChange: Int = 0,
    extremeChange: Int = 0,

    minsChange: Int = 0,
    hourChange: Int = 0,
    dayChange: Int = 0,
    daysChange: Int = 0,
    weekChange: Int = 0): Option[Quest]

  def updateStatus(id: String, newStatus: String): Option[Quest]
  def updateLevel(id: String, newLevel: Int): Option[Quest]

}

