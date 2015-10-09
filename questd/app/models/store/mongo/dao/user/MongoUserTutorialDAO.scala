package models.store.mongo.dao.user
import com.mongodb.casbah.commons.MongoDBObject
import models.domain.user.User
import models.store.dao.user.UserTutorialDAO
import models.store.mongo.helpers.BaseMongoDAO

/**
 * Mongo DAO implementation.
 */
trait MongoUserTutorialDAO extends UserTutorialDAO {
  this: BaseMongoDAO[User] =>

  /**
   *
   */
  def addClosedTutorialElement(id: String, platform: String, elementId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          s"profile.tutorialStates.$platform.closedElementIds" -> elementId)))
  }

  /**
   *
   */
  def addTutorialTaskAssigned(id: String, platform: String, taskId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          s"profile.tutorialStates.$platform.usedTutorialTaskIds" -> taskId)))
  }

  /**
   * @inheritdoc
   */
  def addTutorialQuestAssigned(id: String, platform: String, questId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          s"profile.tutorialStates.$platform.usedTutorialQuestIds" -> questId)))
  }

  /**
   * @inheritdoc
   */
  def setDailyTasksSuppressed(id: String, platform: String, suppressed: Boolean): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          s"profile.tutorialStates.$platform.dailyTasksSuppression" -> suppressed)))
  }
}
