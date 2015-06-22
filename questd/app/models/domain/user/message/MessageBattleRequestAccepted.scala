package models.domain.user.message

import scala.language.implicitConversions

/**
 * A message about accepted battle challenge.
 * @param opponentSolutionId id of a solution we do not want to battle with.
 */
case class MessageBattleRequestAccepted (opponentSolutionId: String)

/**
 * Companion object
 */
object MessageBattleRequestAccepted {
  implicit def toMessage(a: MessageBattleRequestAccepted): Message = {
    Message(
      messageType = MessageType.BattleRequestAccepted,
      data = Map("opponentSolutionId" -> a.opponentSolutionId))
  }
}
