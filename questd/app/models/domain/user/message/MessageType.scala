package models.domain.user.message

/**
 * All types of messages.
 */
object MessageType extends Enumeration {
  // system
  val AllTasksCompleted = Value
  val TaskCompleted = Value
  val BattleRequestAccepted = Value
  val BattleRequestRejected = Value
  val FriendshipAccepted = Value
  val FriendshipRejected = Value
  val FriendshipRemoved = Value
  val DailyResultsReady = Value

  // user
  val Information = Value
}

// TODO: add and use message "daily results ready"
