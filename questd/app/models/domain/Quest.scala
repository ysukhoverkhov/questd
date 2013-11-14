package models.domain


case class Quest(
  id: String,
  info: QuestInfo = QuestInfo()) extends ID
