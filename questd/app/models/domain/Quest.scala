package models.domain

import models.domain.base.ID
import java.util.Date


object QuestStatus extends Enumeration {
  val InRotation, RatingBanned, CheatingBanned, IACBanned, OldBanned = Value
}

case class Quest(
  id: String = ID.generateUUID(),
  cultureId: String,
  lastModDate: Date = new Date(),
  info: QuestInfo,
  rating: QuestRating = QuestRating(),
  status: QuestStatus.Value = QuestStatus.InRotation) extends ID

