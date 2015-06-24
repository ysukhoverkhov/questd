package models.domain.user.profile

import models.domain.base.ID
import models.domain.common.Assets


case class Task(
  id: String = ID.generateUUID(),
  taskType: TaskType.Value,
  description: String,
  reward: Assets = Assets(),
  requiredCount: Int,
  currentCount: Int = 0,
  tutorialTaskId: Option[String] = None,
  triggersReward: Boolean = true) extends ID

