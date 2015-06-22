package models.domain.user.message

import models.domain.base.ID

/**
 * A message to be displayed to user.
 * In this form it's stored in DB and sent to client.
 */
case class Message (
  id: String = ID.generateUUID(),
  messageType: MessageType.Value,
  data: Map[String, String] = Map.empty) extends ID
