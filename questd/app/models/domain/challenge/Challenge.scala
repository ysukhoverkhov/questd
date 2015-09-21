package models.domain.challenge

import java.util.Date

import models.domain.base.ID

/**
 * A challenge we send to someone.
 */
case class Challenge (
  id: String = ID.generate,
  myId: String,
  opponentId: String,
  questId: String,
  mySolutionId: Option[String] = None,
  opponentSolutionId: Option[String] = None,
  creationDate: Date = new Date(),
  status: ChallengeStatus.Value) extends ID
