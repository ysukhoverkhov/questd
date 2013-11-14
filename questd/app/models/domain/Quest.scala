package models.domain

import models.domain.base._

case class QuestID(id: String = "") extends BaseID[String]

case class Quest(
  id: QuestID,
  info: QuestInfo = QuestInfo())
