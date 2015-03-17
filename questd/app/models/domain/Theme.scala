package models.domain

import models.domain.base.ID
import java.util.Date

case class Theme(
  id: String = ID.generateUUID(),
  cultureId: String, 
  info: ThemeInfo,
  lastUseDate: Date = new Date(0)) extends ID

