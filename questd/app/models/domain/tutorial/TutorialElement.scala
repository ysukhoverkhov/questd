package models.domain.tutorial

import models.domain.base.ID

/**
 * One element of a tutorial.
 */
case class TutorialElement(
  id: String = ID.generateUUID(),
  actions: List[TutorialAction],
  serverActions: List[TutorialServerAction] = List.empty,
  conditions: List[TutorialCondition] = List.empty,
  triggers: List[TutorialTrigger],
  crud: TutorialElementCRUD = TutorialElementCRUD()
  ) extends ID
