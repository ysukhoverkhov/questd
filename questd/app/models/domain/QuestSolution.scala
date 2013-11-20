package models.domain

import models.domain.base.ID

case class QuestSolution(
  id: String = ID.generateUUID(),
  questID: String,
  userID: String,
  info: QuestSolutionInfo) extends ID
