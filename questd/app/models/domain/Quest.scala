package models.domain

import models.domain.base.ID
import java.util.Date


object QuestStatus extends Enumeration {
  val OnVoting, InRotation, RatingBanned, CheatingBanned, IACBanned, OldBanned = Value
}

case class Quest(
  id: String = ID.generateUUID(),
  cultureId: String,
  lastModDate: Date = new Date(),
  approveReward: Assets,
  info: QuestInfo,
  rating: QuestRating = QuestRating(),
  // The field is by group.
  status: QuestStatus.Value = QuestStatus.OnVoting) extends ID

