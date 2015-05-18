package models.domain.user

import java.util.Date

/**
 * Schedules for periodic actions with user's profile.
 */
case class UserSchedules (
  dailyTasks: Date = new Date(0),
  timeLine: Date = new Date(0)
  )
