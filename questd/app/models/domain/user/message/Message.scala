package models.domain.user.message

import java.util.Date

import models.domain.base.ID

/**
 * A message to be displayed to user.
 * In this form it's stored in DB and sent to client.
 */
case class Message (
  id: String = ID.generate,
  messageType: MessageType.Value,
  data: Map[String, String] = Map.empty,
  generatedAt: Date = new Date) extends ID
