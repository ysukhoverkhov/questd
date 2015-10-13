package models.domain.user.profile

import java.util.Date

/**
 * All information what is required by analytics.
 */
case class Analytics(
  profileCreationDate: Date = new Date(),
  source: Option[String] = None)

