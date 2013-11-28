package models.store.dao

import models.domain._

trait QuestDAO extends BaseDAO[Quest] {

  def allWithStatus(stauts: String): Iterator[Quest]

}

