package models.store.mongo.dao.user
import com.mongodb.casbah.commons.MongoDBObject
import models.domain.user.User
import models.store.dao.user.UserDepreciatedDAO
import models.store.mongo.helpers.BaseMongoDAO

/**
 * Mongo DAO implementation.
 */
trait MongoUserDepreciatedDAO extends UserDepreciatedDAO {
  this: BaseMongoDAO[User] =>

  /**
   * @inheritdoc
   */
  def populateMustVoteSolutionsList(userIds: List[String], solutionId: String): Unit = {
    update(
      query = MongoDBObject(
        "id" -> MongoDBObject(
          "$in" -> userIds
        )),
      updateRules = MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "mustVoteSolutions" -> solutionId)),
      multi = true)
  }

  /**
   * @inheritdoc
   */
  def removeMustVoteSolution(id: String, solutionId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "mustVoteSolutions" -> solutionId)))
  }
}
