package models.store.mongo.dao

import scala.language.postfixOps
import scala.language.implicitConversions
import scala.language.reflectiveCalls
import play.Logger
import play.api.db._
import play.api.Play._
import se.radley.plugin.salat._
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.dao._
import com.mongodb.casbah.Imports._
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.CommandResult
import com.mongodb.DBObject

import models.store.mongo.SalatContext._
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._


// TODO: make base DAO with common elments.
// TODO: make base mongo dao to implement base dao.

/**
 * DOA for Config objects
 */
private[mongo] class MongoQuestDAO
  extends QuestDAO
  with ModelCompanion[Quest, ObjectId]
  with BaseMongoDAO[Quest] {

  val dao = new SalatDAO[Quest, ObjectId](collection = mongoCollection("quests")) {}
  final protected val keyFieldName = "id.id"

  def createQuest(o: Quest): Unit = create(o)
  def readQuestByID(key: QuestID): Option[Quest] = read(key)
  def updateQuest(o: Quest): Unit = update(o.id, o)
  def deleteQuest(key: QuestID): Unit = delete(key)
  def allQuests: Iterator[Quest] = all

}

