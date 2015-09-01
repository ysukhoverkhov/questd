package models.domain.user.schedules

import java.util.Date

/**
 * Schedules for periodic actions with user's profile.
 */
case class UserSchedules (
  dailyTasks: Date = new Date(0), // TODO: rename to "nextDailyTasksAt" and do not forget in DAO
  timeLine: Date = new Date(0), // TODO: rename to "nextTimeLineAt" and do not forget in DAO
  lastNotificationSentAt: Date = new Date(0)
  )
