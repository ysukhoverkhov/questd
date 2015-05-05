package models.domain

import java.util.Date

case class QuestCreationContext(
  questCreationCoolDown: Date = new Date(0))

