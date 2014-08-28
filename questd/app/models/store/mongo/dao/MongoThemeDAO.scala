package models.store.mongo.dao

import java.util.Date

import com.mongodb.casbah.commons.MongoDBObject
import models.domain._
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Theme objects
 */
private[mongo] class MongoThemeDAO
  extends BaseMongoDAO[Theme](collectionName = "themes")
  with ThemeDAO {

  /**
   *
   */
  def count(): Long = {
    countByExample(MongoDBObject())
  }

  /**
   *
   */
  def allSortedByUseDate: Iterator[Theme] = {
    findByExample(MongoDBObject(), MongoDBObject("lastUseDate" -> 1))
  }

  /**
   *
   */
  def allWithParams(
    cultureId: Option[String],
    sorted: Boolean,
    skip: Int): Iterator[Theme] = {

    val sortObject = if (sorted) MongoDBObject("lastUseDate" -> 1) else MongoDBObject.empty

    val queryBuilder = MongoDBObject.newBuilder

    if (cultureId != None) {
      queryBuilder += ("cultureId" -> cultureId.get)
    }

    findByExample(
      queryBuilder.result(),
      sortObject,
      skip)
  }

  /**
   *
   */
  def updateLastUseDate(id: String): Option[Theme] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "lastUseDate" -> new Date())))
  }
}
