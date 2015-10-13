package models.store.dao

import models.domain.challenge.{ChallengeStatus, Challenge}

trait ChallengeDAO extends BaseDAO[Challenge] {

  /**
   * Returns challenges what contains both these solutions in any combination
   *
   * @param solutionIds Solutions to search.
   * @return Iterator with found challenges.
   */
  def findBySolutions(solutionIds: (String, String)): Iterator[Challenge]

  /**
   * Returns challenges what are between both users and for the quest.
   *
   * @param participantIds Ids of users participating in challenges in any roles.
   * @param questId Id of quest there is a challenge for.
   * @return Iterator with found challenges.
   */
  def findByParticipantsAndQuest(participantIds: (String, String), questId: String): Iterator[Challenge]

  /**
   * @param myId Return challenges where myId is equal to this.
   * @param opponentId Return challenges where opponentId is equal to this.
   * @param statuses Return challenges with htese statuses only.
   * @param skip Skip this number of challenges.
   * @return Iterator with filtered challenges.
   */
  def allWithParams(
    myId: Option[String] = None,
    opponentId: Option[String] = None,
    statuses: List[ChallengeStatus.Value] = List.empty,
    skip: Int = 0): Iterator[Challenge]


  /**
   * Updates status of battle request.
   *
   * @param id Id of challenge to update
   * @param newStatus New status of challenge.
   * @param opponentSolutionId Id of opponent's solution.
   */
  def updateChallenge(id: String, newStatus: ChallengeStatus.Value, opponentSolutionId: Option[String]): Option[Challenge]
}

