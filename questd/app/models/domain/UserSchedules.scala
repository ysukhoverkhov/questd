package models.domain

import java.util.Date

/**
 * Schedules for periodic actions with user's profile.
 */
case class UserSchedules (
  /**
   * When purchased quests and solutions should be reset.
   */
  purchases: Date = new Date(0),
  dailyTasks: Date = new Date(0)
  )

