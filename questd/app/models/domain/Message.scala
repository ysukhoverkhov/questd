package models.domain

import models.domain.base.ID

object MessageType extends Enumeration {
  val FriendshipAccepted = Value
  val FriendshipRejected = Value
  val FriendshipRemoved = Value
  val Text = Value
}

/**
 * A message to be displayed to user.
 * In this form it's stored in DB and sent to client.
 */
case class Message (
  id: String = ID.generateUUID(),
  messageType: MessageType.Value,
  data: Map[String, String]) extends ID


/**
 * A message about accepted friendship.
 * @param newFriendId id of a user who accepts the friendship.
 */
case class MessageFriendshipAccepted (newFriendId: String)

object MessageFriendshipAccepted {
  implicit def toMessage(a: MessageFriendshipAccepted) = {
    Message(
      messageType = MessageType.FriendshipAccepted,
      data = Map("newFriendId" -> a.newFriendId))
  }
}

/**
 * A message about rejected friendship request.
 * @param rejecterId Id of a potential friend who rejected.
 */
case class MessageFriendshipRejected (rejecterId: String)

object MessageFriendshipRejected {
  implicit def toMessage(a: MessageFriendshipRejected) = {
    Message(
      messageType = MessageType.FriendshipRejected,
      data = Map("rejecterId" -> a.rejecterId))
  }
}

/**
 * A message about removed friendship connection.
 * @param oldFriendId Id of a player who decided not to be a friend with us anymore.
 */
case class MessageFriendshipRemoved (oldFriendId: String)

object MessageFriendshipRemoved {
  implicit def toMessage(a: MessageFriendshipRemoved) = {
    Message(
      messageType = MessageType.FriendshipRemoved,
      data = Map("oldFriendId" -> a.oldFriendId))
  }
}
