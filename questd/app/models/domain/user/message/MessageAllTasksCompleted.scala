package models.domain.user.message

import scala.language.implicitConversions

/**
 * A message about completing all tasks
 */
case class MessageAllTasksCompleted () {
  def toMessage: Message = {
    Message(
      messageType = MessageType.AllTasksCompleted)
  }
}

/**
 * Companion.
 */
object MessageAllTasksCompleted {
  implicit def toMessage(a: MessageAllTasksCompleted): Message = a.toMessage
}
