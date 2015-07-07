package models.domain.user.profile

import java.util.Date

/**
 * Context of creating quest.
 */
case class QuestCreationContext(
  questCreationCoolDown: Date = new Date(0))
