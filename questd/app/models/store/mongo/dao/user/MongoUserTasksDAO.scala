package models.store.mongo.dao.user
import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.profile.{Task, DailyTasks}
import models.store.dao.user.UserTasksDAO
import models.store.mongo.helpers.BaseMongoDAO
import models.store.mongo.SalatContext._

/**
 * Mongo DAO implementation.
 */
trait MongoUserTasksDAO extends UserTasksDAO {
  this: BaseMongoDAO[User] =>

  /**
   * @inheritdoc
   */
  def resetTasks(id: String, newTasks: DailyTasks, resetTasksTimeout: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.dailyTasks" -> grater[DailyTasks].asDBObject(newTasks),
          "schedules.nextDailyTasksAt" -> resetTasksTimeout)))
  }

  /**
   * @inheritdoc
   */
  def addTasks(id: String, newTasks: List[Task], addReward: Option[Assets] = None): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$push" -> MongoDBObject(
      "profile.dailyTasks.tasks" -> MongoDBObject(
        "$each" -> newTasks.map(grater[Task].asDBObject))))

    addReward match {
      case Some(assets) =>
        queryBuilder += ("$inc" -> MongoDBObject(
          "profile.dailyTasks.reward.coins" -> assets.coins,
          "profile.dailyTasks.reward.money" -> assets.money,
          "profile.dailyTasks.reward.rating" -> assets.rating))
      case _ =>
    }

    findAndModify(
      id,
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def incTask(id: String, taskId: String): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "profile.dailyTasks.tasks.id" -> taskId),
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.dailyTasks.tasks.$.currentCount" -> 1)))
  }

  /**
   * @inheritdoc
   */
  def setTasksCompletedFraction(id: String, completedFraction: Float): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.dailyTasks.completed" -> completedFraction)))
  }

  /**
   * @inheritdoc
   */
  def setTasksRewardReceived(id: String, rewardReceived: Boolean): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.dailyTasks.rewardReceived" -> rewardReceived)))
  }
}
