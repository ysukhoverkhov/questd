package models.domain

import java.util.Date
import models.domain.view._

case class QuestCreationContext(
  questCreationCoolDown: Date = new Date(0))

