package models.store.dao

import models.domain._

trait ThemeDAO extends BaseDAO[Theme] {
  def count(): Long

  // TODO: remove me.
  def allSortedByUseDate: Iterator[Theme]

  def allWithParams(
    cultureId: Option[String] = None,
    sorted: Boolean = true,
    skip: Int = 0): Iterator[Theme]

  def updateLastUseDate(id: String): Option[Theme]
}

