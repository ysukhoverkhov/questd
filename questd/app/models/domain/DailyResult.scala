package models.domain

import java.util.Date

/**
 * Result of daily activity for user.
 */
case class DailyResult(
  startOfPeriod: Date
  )
  
  // TODO: do not forget to add first daily result is no one is present on adding thing to daily result.