package models.store.mongo.dao

import models.domain.admin._
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Config objects
 */
private[mongo] class MongoConfigDAO
  extends BaseMongoDAO[ConfigSection](collectionName = "configs")
  with ConfigDAO {

  def readConfig: Configuration = {
    Configuration(
      all.foldLeft[Map[String, ConfigSection]](Map.empty) { (m, v) => m + (v.id -> v) })
  }

}

