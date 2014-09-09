package models.domain

import models.domain.base.ID

case class TutorialTask(
  id: String = ID.generateUUID(),
  taskType: TaskType.Value,
  description: String,
  requiredCount: Int,
  reward: Assets) extends ID {

  def task = Task(
    taskType = taskType,
    description = description,
    requiredCount = requiredCount,
    tutorialTask = Some(this))
}

