package models.domain.user.profile

import java.util.Date

/**
 * All information what is required by analytics.
 */
case class Analytics(
  profileCreationDate: Date = new Date(new Date().getTime - 24*60*60*1000), // TODO: set new date here.
  source: Map[String, String] = Map.empty)

