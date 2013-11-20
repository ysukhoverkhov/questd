package models.store.mongo.dao

import play.Logger

import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._

/**
 * DOA for Config objects
 */
private[mongo] class MongoQuestSolutionDAO
  extends BaseMongoDAO[QuestSolution](collectionName = "solutions")
  with QuestSolutionDAO {

}

