package models.domain

import models.domain.base.ID


case class Quest(
  id: String = ID.generateUUID(),
  // TODO: uncomment me.
  //themeID: String,
  userID: String,
  info: QuestInfo
  ) extends ID
