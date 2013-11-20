package models.domain

import models.domain.base.ID

case class QuestSolution(
  id: String = ID.generateUUID(),
  content: ContentReference) extends ID
