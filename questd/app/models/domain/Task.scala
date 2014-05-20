package models.domain

import java.util.Date


object TaskType extends Enumeration {
  val VoteForSolutions = Value
  val SolveQuest = Value
  val VoteForProposal = Value
  val ProposeQuest = Value
  val LookThroughWinnersOfMyQuests = Value
  val MakeReview = Value
  val Client = Value
}

case class Task(
  taskType: TaskType.Value,
  description: String,
  requiredCount: Int,
  currentCount: Int = 0)

