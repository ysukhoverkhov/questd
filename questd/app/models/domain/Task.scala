package models.domain

import models.domain.base.ID


object TaskType extends Enumeration {
  val LikeSolutions = Value
  val CreateSolution = Value
  val LikeQuests = Value
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
  id: String = ID.generateUUID(),
  taskType: TaskType.Value,
  description: String,
  reward: Assets = Assets(),
  requiredCount: Int,
  currentCount: Int = 0,
  tutorialTaskId: Option[String] = None) extends ID

