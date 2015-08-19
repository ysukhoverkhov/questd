package models.domain.user.message

/**
 * All types of messages.
 */
object MessageMetaInfo  {
  val messagePriority: Map[MessageType.Value, Int] = Map(
    MessageType.AllTasksCompleted -> 1000,
    MessageType.TaskCompleted -> 1000,
    MessageType.BattleRequestAccepted -> 4,
    MessageType.BattleRequestRejected -> 5,
    MessageType.FriendshipAccepted -> 1,
    MessageType.FriendshipRejected -> 2,
    MessageType.FriendshipRemoved -> 3,
    MessageType.DailyResultsReady -> 10,

    // user
    MessageType.Information -> 1000
  )

  val messageLocalizedMessage: Map[MessageType.Value, String] = Map(
    MessageType.AllTasksCompleted -> "TODO",
    MessageType.TaskCompleted ->  "TODO",
    MessageType.BattleRequestAccepted ->  "TODO",
    MessageType.BattleRequestRejected ->  "TODO",
    MessageType.FriendshipAccepted ->  "TODO",
    MessageType.FriendshipRejected ->  "TODO",
    MessageType.FriendshipRemoved ->  "TODO",
    MessageType.DailyResultsReady -> "TODO",

    // user
    MessageType.Information ->  "TODO"
  )

  require(messagePriority.size == MessageType.values.size)
  require(messageLocalizedMessage.size == MessageType.values.size)
}
