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
  extends BaseMongoDAO[ConfigSection](collectionName = "configs", keyFieldName = "name")
  with ConfigDAO {

  def upsertSection(o: ConfigSection): Unit = upsert(o.name, o)

  def deleteSection(name: String): Unit = delete(name)

  def readConfig: Configuration = {
    val a: List[ConfigSection] = List() ++ all

    Configuration(
      a.foldLeft[Map[String, ConfigSection]](Map()) { (m, v) => m + (v.name -> v) })
  }

}

