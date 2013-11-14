package models.store.mongo.dao

import play.Logger

import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._

// TODO: make base DAO with common elments.

/**
 * DOA for Config objects
 */
private[mongo] class MongoQuestDAO
  extends BaseMongoDAO[Quest](collectionName = "quests", keyFieldName = "id.id")
  with QuestDAO {

  def createQuest(o: Quest): Unit = create(o)
  def readQuestByID(key: QuestID): Option[Quest] = read(key)
  def updateQuest(o: Quest): Unit = update(o.id, o)
  def deleteQuest(key: QuestID): Unit = delete(key)
  def allQuests: Iterator[Quest] = all

}

