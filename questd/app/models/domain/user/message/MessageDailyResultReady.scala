package models.domain.user.message


import scala.language.implicitConversions

/**
 * A message about completing all tasks
 */
case class MessageDailyResultReady () {
  def toMessage: Message = {
    Message(
      messageType = MessageType.DailyResultsReady)
  }
}

/**
 * Companion.
 */
object MessageDailyResultReady {
  implicit def toMessage(a: MessageDailyResultReady): Message = a.toMessage
}
