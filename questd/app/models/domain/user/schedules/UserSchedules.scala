package models.domain.user.schedules

import java.util.Date

/**
 * Schedules for periodic actions with user's profile.
 */
case class UserSchedules (
  nextDailyTasksAt: Date = new Date(0),
  nextTimeLineAt: Date = new Date(0),
  lastNotificationSentAt: Date = new Date(0)
  )

