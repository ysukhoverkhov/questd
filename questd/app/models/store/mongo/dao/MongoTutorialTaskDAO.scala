package models.store.mongo.dao

import models.domain.tutorialtask.TutorialTask
import models.store.mongo.helpers._
import models.store.dao._
import models.domain._

/**
 * DOA for Tutorial tasks objects
 */
private[mongo] class MongoTutorialTaskDAO
  extends BaseMongoDAO[TutorialTask](collectionName = "tutorialtasks")
  with TutorialTaskDAO
