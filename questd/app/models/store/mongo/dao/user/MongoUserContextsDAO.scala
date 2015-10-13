package models.store.mongo.dao.user
import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.user.User
import models.store.dao.user.UserContextsDAO
import models.store.mongo.helpers.BaseMongoDAO
import models.view.QuestView
import models.store.mongo.SalatContext._

/**
 * Mongo DAO implementation.
 */
trait MongoUserContextsDAO extends UserContextsDAO {
  this: BaseMongoDAO[User] =>

  /**
   * @inheritdoc
   */
  def setQuestBookmark(id: String, quest: QuestView): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.questSolutionContext.bookmarkedQuest" -> grater[QuestView].asDBObject(quest))
      ))
  }

  /**
   * @inheritdoc
   */
  def updateQuestCreationCoolDown(id: String, coolDown: Date): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$set" -> MongoDBObject(
      "profile.questCreationContext.questCreationCoolDown" -> coolDown))

    findAndModify(
      id,
      queryBuilder.result())
  }
}
