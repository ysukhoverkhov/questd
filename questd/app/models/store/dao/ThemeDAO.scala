package models.store.dao

import models.domain._

trait ThemeDAO extends BaseDAO[Theme] {
  def count(): Long
  
  def allSortedByUseDate: Iterator[Theme]
  
  def updateLastUseDate(id: String): Option[Theme]
}

