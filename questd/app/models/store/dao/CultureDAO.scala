package models.store.dao

import models.domain.culture.Culture

trait CultureDAO extends BaseDAO[Culture] {
  def findByCountry(country: String): Option[Culture]
}

