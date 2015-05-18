package models.domain.tutorialtask

import models.domain.base.ID
import models.domain.common.Assets
import models.domain.user.{Task, TaskType}

/**
 * Task created by tutorial script.
 */
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
    reward = reward,
    tutorialTaskId = Some(this.id))
}
