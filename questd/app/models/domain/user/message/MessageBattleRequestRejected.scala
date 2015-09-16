package models.domain.user.message

import scala.language.implicitConversions

/**
 * A message about rejected battle challenge.
 * @param challengeId id of a solution we do not want to battle with.
 */
case class MessageBattleRequestRejected (challengeId: String)

/**
 * Companion object
 */
object MessageBattleRequestRejected {
  implicit def toMessage(a: MessageBattleRequestRejected): Message = {
    Message(
      messageType = MessageType.BattleRequestRejected,
      data = Map("challengeId" -> a.challengeId))
  }
}
