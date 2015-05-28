package models.domain.user

/**
 * All types of tasks.
 */
object TaskType extends Enumeration {
  val LikeSolutions = Value
  val CreateSolution = Value
  val LikeQuests = Value
  val AddToFollowing = Value
  val CreateQuest = Value
  val VoteReviews = Value
  val SubmitReviewsForResults = Value
  val SubmitReviewsForQuests = Value
  val GiveRewards = Value
  val LookThroughFriendshipProposals = Value
  val Client = Value(1000)
}