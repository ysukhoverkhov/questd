package models.domain.user.message

import scala.language.implicitConversions

/**
 * A message about rejected battle challenge.
 * @param challengeId id of a solution we do not want to battle with.
 */
case class MessageChallengeRejected (challengeId: String)

/**
 * Companion object
 */
object MessageChallengeRejected {
  implicit def toMessage(a: MessageChallengeRejected): Message = {
    Message(
      messageType = MessageType.ChallengeRejected,
      data = Map("challengeId" -> a.challengeId))
  }
}
