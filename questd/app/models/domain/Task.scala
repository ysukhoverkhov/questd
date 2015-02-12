package models.domain


object TaskType extends Enumeration {
  val VoteSolutions = Value
  val CreateSolution = Value
  val VoteQuests = Value
  val AddToFollowing = Value
  val CreateQuest = Value
  val VoteReviews = Value
  val SubmitReviewsForResults = Value
  val SubmitReviewsForProposals = Value
  val GiveRewards = Value
  val LookThroughFriendshipProposals = Value
  val Client = Value(1000)
}

case class Task(
  taskType: TaskType.Value,
  description: String,
  requiredCount: Int,
  currentCount: Int = 0,
  tutorialTask: Option[TutorialTask] = None)

