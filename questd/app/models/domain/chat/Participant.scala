package models.domain.chat

import java.util.Date

/**
 * A single chat participant.
 *
 * Created by Yury on 03.08.2015.
 */
case class Participant (
  userId: String,
  hasUnreadMessages: Boolean = false,
  lastReadMessageDate: Date = new Date()
  )
