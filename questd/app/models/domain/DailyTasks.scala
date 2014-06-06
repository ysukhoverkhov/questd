package models.domain

/**
 * Tasks for a day for user.
 */
case class DailyTasks(
  tasks: List[Task] = List(),
  completed: Float = 0f,
  reward: Assets = Assets(),
  rewardreceived: Boolean = false
  )

