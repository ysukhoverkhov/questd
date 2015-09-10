package models.domain.user.message

import scala.language.implicitConversions


/**
 * A message about rejected friendship request.
 * @param rejecterId Id of a potential friend who rejected.
 */
case class MessageFriendshipRejected (rejecterId: String)


/**
 * Companion.
 */
object MessageFriendshipRejected {
  implicit def toMessage(a: MessageFriendshipRejected): Message = {
    Message(
      messageType = MessageType.FriendshipRejected,
      data = Map("rejecterId" -> a.rejecterId))
  }
}
