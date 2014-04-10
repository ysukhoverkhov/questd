package models.store.mongo.dao

import play.Logger
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import com.mongodb.casbah.commons.MongoDBObject

/**
 * DOA for Theme objects
 */
private[mongo] class MongoThemeDAO
  extends BaseMongoDAO[Theme](collectionName = "themes")
  with ThemeDAO {

  def count(): Long = {
    countByExample(MongoDBObject())
  }
}

