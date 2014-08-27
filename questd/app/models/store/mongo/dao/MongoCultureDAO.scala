package models.store.mongo.dao

import models.domain._
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Culture objects
 */
private[mongo] class MongoCultureDAO
  extends BaseMongoDAO[Culture](collectionName = "cultures")
  with CultureDAO {

  /**
   * Searches culture by containing country.
   */
  def findByCountry(country: String): Option[Culture] = {
    readByExample("countries", country)
  }
}

