package models.domain.tutorial

import models.domain.base.ID

/**
 * One element of a tutorial.
 */
case class TutorialElement(
  id: String = ID.generateUUID(),
  actions: List[TutorialAction],
  conditions: List[TutorialCondition] = List.empty,
  triggers: List[TutorialTrigger]
  ) extends ID
