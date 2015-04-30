package models.domain

import models.domain.base.ID

case class TutorialElement(
  id: String = ID.generateUUID(),
  action: TutorialAction,
  conditions: List[TutorialCondition] = List.empty,
  triggers: TutorialTrigger
  ) extends ID

