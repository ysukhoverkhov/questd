package models.store.mongo.dao

import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain._
import models.store.dao._
import models.store.mongo.helpers._
import models.store.mongo.SalatContext._

/**
 * DOA for Tutorial tasks objects
 */
private[mongo] class MongoTutorialDAO
  extends BaseMongoDAO[Tutorial](collectionName = "tutorials")
  with TutorialDAO {

  /**
   * @inheritdoc
   */
  def addElement(id: String, tutorialElement: TutorialElement) = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "elements" -> grater[TutorialElement].asDBObject(tutorialElement))))
  }

  /**
   * @inheritdoc
   */
  def deleteElement(id: String, tutorialElementId: String): Option[Tutorial] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "elements" -> MongoDBObject("id" -> tutorialElementId))))
  }
}
