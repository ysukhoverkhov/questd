package models.store.mongo.dao

import play.Logger
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import com.mongodb.casbah.commons.MongoDBObject
import java.util.Date

/**
 * DOA for Theme objects
 */
private[mongo] class MongoThemeDAO
  extends BaseMongoDAO[Theme](collectionName = "themes")
  with ThemeDAO {

  def count(): Long = {
    countByExample(MongoDBObject())
  }
  
  def allSortedByUseDate: Iterator[Theme] = {
    findByExample(MongoDBObject(), MongoDBObject("lastUseDate" -> 1))
  }
  
  def updateLastUseDate(id: String): Option[Theme] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "lastUseDate" -> new Date()))))
  }
}

