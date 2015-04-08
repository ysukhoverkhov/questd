package models.store.mongo.dao

import models.domain._
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Tutorial tasks objects
 */
private[mongo] class MongoTutorialDAO
  extends BaseMongoDAO[Tutorial](collectionName = "tutorials")
  with TutorialDAO
