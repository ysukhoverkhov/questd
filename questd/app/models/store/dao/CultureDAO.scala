package models.store.dao

import models.domain._

trait CultureDAO extends BaseDAO[Culture] {
  def findByCountry(country: String): Option[Culture]
}

