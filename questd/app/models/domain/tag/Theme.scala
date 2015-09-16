package models.domain.tag

import java.util.Date

import models.domain.base.ID

/**
 * A redundant theme what will be removed.
 */
case class Theme(
  id: String = ID.generate,
  cultureId: String,
  info: ThemeInfo,
  lastUseDate: Date = new Date(0)) extends ID
