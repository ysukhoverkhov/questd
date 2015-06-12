package models.domain.user.message

/**
 * All types of messages.
 */
object MessageType extends Enumeration {
  val FriendshipAccepted = Value
  val FriendshipRejected = Value
  val FriendshipRemoved = Value
  val AllTasksCompleted = Value
  val TaskCompleted = Value
  val Information = Value
}
