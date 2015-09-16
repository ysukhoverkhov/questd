package models.store.dao

import models.domain.challenge.Challenge

trait ChallengeDAO extends BaseDAO[Challenge] {

//  def allWithParams(
//    commentedObjectId: List[String] = List.empty,
//    skip: Int = 0
//  ): Iterator[Challenge]


//  /**
//   * Adds battle request to user.
//   *
//   * @param id Id of user to add request to.
//   * @param battleRequest Request to add.
//   * @return Modified user.
//   */
//  def addBattleRequest(id: String, battleRequest: Challenge): Option[User]
//
//  /**
//   * Updates status of battle request.
//   *
//   * @param id Id of user to update request for.
//   * @param mySolutionId Id of user's challenged solution.
//   * @param opponentSolutionId Id of opponent's sopution.
//   * @param status new status.
//   */
//  def updateBattleRequest(id: String, mySolutionId: String, opponentSolutionId: String, status: String): Option[User]
}

