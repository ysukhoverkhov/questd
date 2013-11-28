package models.domain

import models.domain.base.ID


object QuestStatus extends Enumeration {
  val OnVoting, InRotation, RatingBanned, CheatingBanned, IACBanned, OldBanned = Value
}

case class Quest(
  id: String = ID.generateUUID(),
  //  themeID: String, // perhaps it's better to store theme here. or, perhaps, in quest info as in a object we pass to client.
  userID: String,
  info: QuestInfo,
  rating: QuestRating = QuestRating(),
  // The field is by group.
  status: String = QuestStatus.OnVoting.toString,
  level: Int = 0) extends ID

  // TODO make content type as a string.
