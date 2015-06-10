package models.domain.user.message

import scala.language.implicitConversions

/**
 * A message about accepted friendship.
 * @param newFriendId id of a user who accepts the friendship.
 */
case class MessageFriendshipAccepted (newFriendId: String)

/**
 * Companion object
 */
object MessageFriendshipAccepted {
  implicit def toMessage(a: MessageFriendshipAccepted): Message = {
    Message(
      messageType = MessageType.FriendshipAccepted,
      data = Map("newFriendId" -> a.newFriendId))
  }
}
