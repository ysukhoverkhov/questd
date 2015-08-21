package models.domain.user.battlerequests

/**
 * A request for battle we send to someone.
 */
case class BattleRequest (
  opponentId: String,
  mySolutionId: String,
  opponentSolutionId: String,
  status: BattleRequestStatus.Value
  )

