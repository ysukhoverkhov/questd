package models.store.mongo.dao

import play.Logger

import models.store.mongo.helpers._
import models.store.dao._
import models.store._

import models.domain._

/**
 * DOA for Theme objects
 */
private[mongo] class MongoThemeDAO
  extends BaseMongoDAO[Theme](collectionName = "themes")
  with ThemeDAO {

  /**
   * Create
   */
  def createTheme(o: Theme): Unit = create(o)

  /**
   * Read by id
   */
  def readThemeByID(key: String): Option[Theme] = readByID(key)

  /**
   * Update by id.
   */
  def updateTheme(u: Theme): Unit = update(u.id, u)

  /**
   * Delete by id
   */
  def deleteTheme(key: String): Unit = delete(key)

  /**
   * All objects
   */
  def allThemes: Iterator[Theme] = all

}

