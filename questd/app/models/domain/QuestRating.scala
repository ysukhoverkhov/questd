package models.domain

/**
 * Rating of a quest used during voting.
 */
case class QuestRating(
    // The filed is by group.
  points: Int = 0,
  cheatingPoints: Int = 0,
  iacrating: IAContentRating = IAContentRating(),
  difficultyRating: QuestDifficultyRating = QuestDifficultyRating(),
  durationRating: QuestDurationRating = QuestDurationRating(), 
  votersCount: Int = 0)
