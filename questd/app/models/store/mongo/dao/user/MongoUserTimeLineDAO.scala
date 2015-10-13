package models.store.mongo.dao.user
import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.user.User
import models.domain.user.timeline.{TimeLineReason, TimeLineEntry}
import models.store.dao.user.UserTimeLineDAO
import models.store.mongo.helpers.BaseMongoDAO
import models.store.mongo.SalatContext._

/**
 * Mongo DAO implementation.
 */
trait MongoUserTimeLineDAO extends UserTimeLineDAO {
  this: BaseMongoDAO[User] =>

  /**
   * @inheritdoc
   */
  def setTimeLinePopulationTime(id: String, time: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "schedules.nextTimeLineAt" -> time)))
  }

  /**
   * @inheritdoc
   */
  def addEntryToTimeLine(id: String, entry: TimeLineEntry): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "timeLine" ->
            MongoDBObject(
              "$each" -> List(grater[TimeLineEntry].asDBObject(entry)),
              "$position" -> 0))))
  }

  /**
   * @inheritdoc
   */
  def addEntryToTimeLineMulti(ids: List[String], entry: TimeLineEntry): Unit = {
    update(
      query = MongoDBObject(
        "id" -> MongoDBObject(
          "$in" -> ids)),
      updateRules = MongoDBObject(
        "$push" -> MongoDBObject(
          "timeLine" ->
            MongoDBObject(
              "$each" -> List(grater[TimeLineEntry].asDBObject(entry)),
              "$position" -> 0))),
      multi = true)
  }

  /**
   * @inheritdoc
   */
  def removeEntryFromTimeLineByObjectId(id: String, objectId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "timeLine" -> MongoDBObject(
            "objectId" -> objectId))))
  }

  def updateTimeLineEntry(id: String, entryId: String, reason: TimeLineReason.Value): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "timeLine.id" -> entryId),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "timeLine.$.reason" -> reason.toString)))
  }
}
