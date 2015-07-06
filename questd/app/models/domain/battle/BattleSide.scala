package models.domain.battle

/**
 * Info about one side of battle.
 */
case class BattleSide(
  solutionId: String,
  authorId: String,
  isWinner: Boolean = false,
  pointsRandom: Int = 0,
  pointsFriends: Int = 0
  )
