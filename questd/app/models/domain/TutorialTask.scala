package models.domain

import models.domain.base.ID

case class TutorialTask (
  id: String = ID.generateUUID,
  taskType: TaskType.Value,
  description: String,
  requiredCount: Int,
  reward: Assets) extends ID 

