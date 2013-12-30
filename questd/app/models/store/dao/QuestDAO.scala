package models.store.dao

import models.domain._

trait QuestDAO extends BaseDAO[Quest] {

  def allWithStatus(status: String, minLevel: Int, maxLevel: Int): Iterator[Quest]

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

