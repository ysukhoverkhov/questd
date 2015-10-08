package models.domain.user.message

/**
 * All types of messages.
 */
object MessageMetaInfo  {
  val messagePriority: Map[MessageType.Value, Int] = Map(
    MessageType.AllTasksCompleted -> 1000,
    MessageType.TaskCompleted -> 1000,
    MessageType.BattleRequestAccepted -> 10,
    MessageType.BattleRequestRejected -> 11,
    MessageType.FriendshipAccepted -> 1,
    MessageType.FriendshipRejected -> 2,
    MessageType.FriendshipRemoved -> 3,
    MessageType.DailyResultsReady -> 20,

    // user
    MessageType.Information -> 1000,
    MessageType.NewChatMessage -> 20,
    MessageType.FriendRegistered -> 5
  )

  val messageLocalizedMessage: Map[MessageType.Value, String] = Map(
    MessageType.AllTasksCompleted -> "NOTIFICATION_ALL_TASKS_COMPLETED",
    MessageType.TaskCompleted ->  "NOTIFICATION_TASK_COMPLETED",
    MessageType.BattleRequestAccepted ->  "NOTIFICATION_BATTLE_REQUEST_ACCEPTED",
    MessageType.BattleRequestRejected ->  "NOTIFICATION_BATTLE_REQUEST_REJECTED",
    MessageType.FriendshipAccepted ->  "NOTIFICATION_FRIENDSHIP_ACCEPTED",
    MessageType.FriendshipRejected ->  "NOTIFICATION_FRIENDSHIP_REJECTED",
    MessageType.FriendshipRemoved ->  "NOTIFICATION_FRIENDSHIP_REMOVED",
    MessageType.DailyResultsReady -> "NOTIFICATION_DAILY_RESULTS_READY",

    // user
    MessageType.Information -> "NOTIFICATION_INFORMATION",
    MessageType.NewChatMessage -> "NOTIFICATION_NEW_CHAT_MESSAGE",
    MessageType.FriendRegistered -> "NOTIFICATION_FRIEND_REGISTERED"
  )

  require(messagePriority.size == MessageType.values.size)
  require(messageLocalizedMessage.size == MessageType.values.size)
}
