package models.domain

import models.domain.base.ID


object QuestStatus extends Enumeration {
  val OnVoting, InRotation, RatingBanned, CheatingBanned, IACBanned, OldBanned = Value
}

case class Quest(
  id: String = ID.generateUUID(),
  themeID: String,
  authorUserID: String,
  info: QuestInfo,
  rating: QuestRating = QuestRating(),
  // The field is by group.
  status: String = QuestStatus.OnVoting.toString) extends ID

