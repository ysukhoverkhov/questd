package models.domain.quest

import java.util.Date

import models.domain.base.ID

/**
 * Quest created by users.
 */
case class Quest(
  id: String = ID.generate,
  cultureId: String,
  lastModDate: Date = new Date(),
  info: QuestInfo,
  rating: QuestRating = QuestRating(),
  solutionsCount: Int = 0,
  status: QuestStatus.Value = QuestStatus.InRotation) extends ID
