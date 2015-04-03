package models.domain

/**
 * Tasks for a day for user.
 */
case class DailyTasks(
  tasks: List[Task] = List.empty,
  completed: Float = 0f,
  reward: Assets = Assets(),
  rewardReceived: Boolean = false
  )

