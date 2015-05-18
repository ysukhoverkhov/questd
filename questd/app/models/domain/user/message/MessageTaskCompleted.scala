package models.domain.user.message

import scala.language.implicitConversions


/**
 * A message about completing a tasks
 */
case class MessageTaskCompleted (taskId: String)

/**
 * Companion.
 */
object MessageTaskCompleted {
  implicit def toMessage(a: MessageTaskCompleted): Message = {
    Message(
      messageType = MessageType.TaskCompleted,
      data = Map("taskId" -> a.taskId))
  }
}

