package models.store.dao

import models.domain.quest.{Quest, QuestStatus}

trait QuestDAO extends BaseDAO[Quest] {

  /**
   * Count quests with given status.
   */
  def countWithStatus(status: String): Long

  /**
   * All with status and users.
   *
   * @param ids list of identifiers chose quest from.
   */
  def allWithParams(
    status: List[QuestStatus.Value] = List.empty,
    authorIds: List[String] = List.empty,
    authorIdsExclude: List[String] = List.empty,
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List.empty,
    idsExclude: List[String] = List.empty,
    cultureId: Option[String] = None,
    withSolutions: Boolean = false): Iterator[Quest] // TODO: last param should be Option and check for true and false both.

  /**
   * Update quest's points.
   */
  def updatePoints(
    id: String,
    timelinePointsChange: Int,
    likesChange: Int = 0,
    votersCountChange: Int = 0,
    cheatingChange: Int = 0,
    spamChange: Int = 0,
    pornChange: Int = 0): Option[Quest]

  /**
   * Updaes status of the quest.
   *
   * @param id Of the quest to change status.
   * @param newStatus New status to set.
   * @return Updated quest.
   */
  def updateStatus(id: String, newStatus: QuestStatus.Value): Option[Quest]

  def updateInfo(id: String, newLevel: Int): Option[Quest]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit

  /**
   * Adds solution id to list of quest's solutions.
   *
   * @param id Id of a quest to add solution id to.
   * @return updated quest.
   */
  def addSolution(id: String): Option[Quest]

}

