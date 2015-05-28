package models.domain.battle

/**
 * Info about one side of battle.
 */
case class BattleSide(
  solutionId: String,
  authorId: String,
  isWinner: Boolean = false,
  points: Int = 0
  )
