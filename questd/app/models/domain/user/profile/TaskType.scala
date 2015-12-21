package models.domain.user.profile

/**
 * All types of tasks.
 */
object TaskType extends Enumeration {
  val LikeSolutions = Value
  val CreateSolution = Value
  val CreateVideoSolution = Value
  val LikeQuests = Value
  val AddToFollowing = Value
  val CreateQuest = Value
  val CreateVideoQuest = Value
  val ChallengeBattle = Value
  val PostComments = Value
  val VoteComments = Value

  val VoteBattle = Value

  val GiveRewards = Value
  val LookThroughFriendshipProposals = Value

  val Custom = Value(1000)
}
