package models.domain.user

import models.domain.common.Assets
import models.domain.quest.QuestStatus

/**
 * Result of quest creation.
 */
case class QuestResult(
    questId: String,
    reward: Option[Assets],
    penalty: Option[Assets],
    status: QuestStatus.Value)
