package models.domain

import java.util.Date

/**
 * Schedules for periodic actions with user's profile.
 */
case class UserSchedules (
  purchases: Date = new Date(0),
  statShift: Date = new Date(0))

