package models.domain.user.message

/**
 * All types of messages.
 */
object MessageType extends Enumeration {
  // system
  val AllTasksCompleted = Value
  val TaskCompleted = Value
  val ChallengeAccepted = Value
  val ChallengeRejected = Value
  val FriendshipAccepted = Value
  val FriendshipRejected = Value
  val FriendshipRemoved = Value
  val DailyResultsReady = Value

  // user
  val Information = Value
  val NewChatMessage = Value
  val FriendRegistered = Value

  // TODO: remove in 0.50.02
  // TODO: add removing of these messages to cleanup in 0.50.01.
  val BattleRequestAccepted = Value
  val BattleRequestRejected = Value
}
