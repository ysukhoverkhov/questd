package models.domain

import scala.language.implicitConversions
import models.domain.base.ID

object MessageType extends Enumeration {
  val FriendshipAccepted = Value
  val FriendshipRejected = Value
  val FriendshipRemoved = Value
  val AllTasksCompleted = Value
  val TaskCompleted = Value
  val Information = Value
}

/**
 * A message to be displayed to user.
 * In this form it's stored in DB and sent to client.
 */
case class Message (
  id: String = ID.generateUUID(),
  messageType: MessageType.Value,
  data: Map[String, String] = Map.empty) extends ID


/**
 * A message about accepted friendship.
 * @param newFriendId id of a user who accepts the friendship.
 */
case class MessageFriendshipAccepted (newFriendId: String)

object MessageFriendshipAccepted {
  implicit def toMessage(a: MessageFriendshipAccepted): Message = {
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
  implicit def toMessage(a: MessageFriendshipRejected): Message = {
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
  implicit def toMessage(a: MessageFriendshipRemoved): Message = {
    Message(
      messageType = MessageType.FriendshipRemoved,
      data = Map("oldFriendId" -> a.oldFriendId))
  }
}

/**
 * A message about completing all tasks
 */
case class MessageAllTasksCompleted () {
  def toMessage: Message = {
    Message(
      messageType = MessageType.AllTasksCompleted)
  }
}

object MessageAllTasksCompleted {
  implicit def toMessage(a: MessageAllTasksCompleted): Message = a.toMessage
}


/**
 * A message about completing a tasks
 */
case class MessageTaskCompleted (taskId: String)

object MessageTaskCompleted {
  implicit def toMessage(a: MessageTaskCompleted): Message = {
    Message(
      messageType = MessageType.TaskCompleted,
      data = Map("taskId" -> a.taskId))
  }
}

/**
 * A message about removed friendship connection.
 */
case class MessageInformation (text: String, url: Option[String])

object MessageInformation {
  implicit def toMessage(a: MessageInformation): Message = {
    Message(
      messageType = MessageType.Information,
      data = a.url.fold[Map[String, String]](Map.empty)(url => Map("url" -> url)) ++ Map("text" -> a.text))
  }
}
