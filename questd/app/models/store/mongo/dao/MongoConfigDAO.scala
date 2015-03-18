package models.store.mongo.dao

import play.Logger
import models.store.mongo.helpers._
import models.store.dao._
import models.domain._
import models.domain.admin._

/**
 * DOA for Config objects
 */
private[mongo] class MongoConfigDAO
  extends BaseMongoDAO[ConfigSection](collectionName = "configs")
  with ConfigDAO {

  def readConfig: Configuration = {
    val a: List[ConfigSection] = List.empty ++ all

    Configuration(
      a.foldLeft[Map[String, ConfigSection]](Map()) { (m, v) => m + (v.id -> v) })
  }

}

