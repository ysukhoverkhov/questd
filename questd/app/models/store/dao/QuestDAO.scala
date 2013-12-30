package models.store.dao

import models.domain._

trait QuestDAO extends BaseDAO[Quest] {

  def allWithStatus(status: String, minLevel: Int, maxLevel: Int): Iterator[Quest]

  def updatePoints(id: String, pointsChange: Int, votersCountChange: Int): Option[Quest]

}

