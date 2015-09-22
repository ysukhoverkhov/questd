package models.domain.tutorialtask

import models.domain.base.ID
import models.domain.common.Assets
import models.domain.user.profile.{TaskType, Task}

/**
 * Task created by tutorial script.
 */
case class TutorialTask(
  id: String = ID.generate,
  taskType: TaskType.Value,
  description: String,
  requiredCount: Int,
  reward: Assets,
  triggersReward: Boolean) extends ID {

  def task = Task(
    taskType = taskType,
    description = description,
    requiredCount = requiredCount,
    reward = reward,
    tutorialTaskId = Some(this.id),
    triggersReward = triggersReward)
}
