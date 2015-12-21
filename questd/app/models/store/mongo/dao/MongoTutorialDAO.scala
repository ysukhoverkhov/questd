package models.store.mongo.dao

import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.tutorial.{Tutorial, TutorialElement}
import models.store.dao._
import models.store.mongo.SalatContext._
import models.store.mongo.helpers._

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

  /**
   * @inheritdoc
   */
  def updateElement(id: String, tutorialElement: TutorialElement): Option[Tutorial] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "elements.id" -> tutorialElement.id),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "elements.$" -> grater[TutorialElement].asDBObject(tutorialElement))))
  }
}
