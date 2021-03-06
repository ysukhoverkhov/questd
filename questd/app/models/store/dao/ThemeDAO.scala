package models.store.dao

import models.domain._
import models.domain.tag.Theme

trait ThemeDAO extends BaseDAO[Theme] {
  def count: Long

  def allWithParams(
    cultureId: Option[String] = None,
    sorted: Boolean = true,
    skip: Int = 0): Iterator[Theme]

  def updateLastUseDate(id: String): Option[Theme]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit
}

