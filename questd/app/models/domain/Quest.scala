package models.domain


case class Quest(
  id: String = ID.generateUUID(),
  info: QuestInfo) extends ID
