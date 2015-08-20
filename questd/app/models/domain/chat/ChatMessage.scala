package models.domain.chat

import java.util.Date

import models.domain.base.ID

/**
 * A single chat message.
 *
 * Created by Yury on 03.08.2015.
 */
case class ChatMessage (
  id: String = ID.generateUUID(),
  senderId: String,
  conversationId: String,
  message: String,
  creationDate: Date = new Date()
  ) extends ID
