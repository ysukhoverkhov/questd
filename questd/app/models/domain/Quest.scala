package models.domain

import models.domain.base.ID
import java.util.Date


object QuestStatus extends Enumeration {
  val OnVoting, InRotation, RatingBanned, CheatingBanned, IACBanned, OldBanned = Value
}

case class Quest(
  id: String = ID.generateUUID(),
  lastModDate: Date = new Date(),
  authorUserId: String,
  approveReward: Assets,
  info: QuestInfo,
  rating: QuestRating = QuestRating(),
  // The field is by group.
  status: String = QuestStatus.OnVoting.toString) extends ID

