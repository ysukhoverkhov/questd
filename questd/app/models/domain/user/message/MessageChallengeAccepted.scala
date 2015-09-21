package models.domain.user.message

import scala.language.implicitConversions

/**
 * A message about accepted battle challenge.
 * @param challengeId id of a solution we do not want to battle with.
 */
case class MessageChallengeAccepted (challengeId: String)

/**
 * Companion object
 */
object MessageChallengeAccepted {
  implicit def toMessage(a: MessageChallengeAccepted): Message = {
    Message(
      messageType = MessageType.ChallengeAccepted,
      data = Map("challengeId" -> a.challengeId))
  }
}
