package models.domain

import java.util.Date

case class TimelineEntry (
                           id: String,
                           dateAdded: Date,
                           reason: String, // TODO: change reason here.
                           objectType: String, // TODO: object type
                           objectId: String )

/**
 *
 */
case class Timeline(
  )

