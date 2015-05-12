package models.domain.battle

import java.util.Date

import models.domain.base.ID

/**
 * A battle our solution participated to.
 */
case class Battle(
  id: String = ID.generateUUID(),
  info: BattleInfo,
  lastModDate: Date = new Date(),
  level: Int,
  vip: Boolean,
  cultureId: String
  ) extends ID
