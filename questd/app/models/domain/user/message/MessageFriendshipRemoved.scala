package models.domain.user.message

import scala.language.implicitConversions

/**
 * A message about removed friendship connection.
 * @param oldFriendId Id of a player who decided not to be a friend with us anymore.
 */
case class MessageFriendshipRemoved (oldFriendId: String)

/**
 * Companion.
 */
object MessageFriendshipRemoved {
  implicit def toMessage(a: MessageFriendshipRemoved): Message = {
    Message(
      messageType = MessageType.FriendshipRemoved,
      data = Map("oldFriendId" -> a.oldFriendId))
  }
}
