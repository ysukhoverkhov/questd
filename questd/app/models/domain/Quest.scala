package models.domain

import models.domain.base.ID


case class Quest(
  id: String = ID.generateUUID(),
  info: QuestInfo) extends ID
