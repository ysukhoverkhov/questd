package models.domain.user.message

import scala.language.implicitConversions


/**
 * A message about registration of our friend.
 */
case class MessageFriendRegistered (friendUserId: String)


/**
 * Companion object
 */
object MessageFriendRegistered {
  implicit def toMessage(a: MessageFriendRegistered): Message = {
    Message(
      messageType = MessageType.FriendRegistered,
      data = Map("friendUserId" -> a.friendUserId))
  }
}
