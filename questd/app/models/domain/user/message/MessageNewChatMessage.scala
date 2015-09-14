package models.domain.user.message

import scala.language.implicitConversions


/**
 * A message about new message.
 */
case class MessageNewChatMessage ()


/**
 * Companion object
 */
object MessageNewChatMessage {
  implicit def toMessage(a: MessageNewChatMessage): Message = {
    Message(
      messageType = MessageType.NewChatMessage)
  }
}
