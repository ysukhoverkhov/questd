package models.domain

import java.util.Date


object TaskType extends Enumeration {
  val VoteQuestSolutions = Value
  val SubmitQuestResult = Value
  val AddToFollowing = Value
  val VoteQuestProposals = Value
  val SubmitQuestProposal = Value
  val VoteReviews = Value
  val SubmitReviewsForResults = Value
  val SubmitReviewsForProposals = Value
  val GiveRewards = Value
  val LookThroughWinnersOfMyQuests = Value
  val LookThroughFriendshipProposals = Value
  val Client = Value(1000)
}

case class Task(
  taskType: TaskType.Value,
  description: String,
  requiredCount: Int,
  currentCount: Int = 0,
  tutorialTask: Option[TutorialTask] = None)

