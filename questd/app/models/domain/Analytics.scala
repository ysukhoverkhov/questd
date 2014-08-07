package models.domain

import java.util.Date

/**
 * All information what is required by analytics.
 */
case class Analytics(
  val profileCreationDate: Date = new Date())
    
