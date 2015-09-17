package models.domain.user.message


import scala.language.implicitConversions

/**
 * A message about completing all tasks
 */
case class MessageDailyResultsReady () {
  def toMessage: Message = {
    Message(
      messageType = MessageType.DailyResultsReady)
  }
}

/**
 * Companion.
 */
object MessageDailyResultsReady {
  implicit def toMessage(a: MessageDailyResultsReady): Message = a.toMessage
}
