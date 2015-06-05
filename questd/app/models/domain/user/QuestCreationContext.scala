package models.domain.user

import java.util.Date

/**
 * Context of creating quest.
 */
case class QuestCreationContext(
  questCreationCoolDown: Date = new Date(0))
