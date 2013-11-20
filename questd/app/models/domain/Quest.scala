package models.domain

import models.domain.base.ID


case class Quest(
  id: String = ID.generateUUID(),
//  themeID: String,
  userID: String,
  info: QuestInfo
  ) extends ID
