package models.store.mongo.dao

import play.Logger
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import com.mongodb.casbah.commons.MongoDBObject
import java.util.Date

/**
 * DOA for Tutorial tasks objects
 */
private[mongo] class MongoTutorialTaskDAO
  extends BaseMongoDAO[TutorialTask](collectionName = "tutorialtasks")
  with TutorialTaskDAO 
