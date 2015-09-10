package models.domain.user.dailyresults

import models.domain.common.Assets
import models.domain.quest.QuestStatus

/**
 * Result of quest creation.
 */
case class QuestResult(
  questId: String,
  reward: Assets,
  status: QuestStatus.Value)
