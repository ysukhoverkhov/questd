package models.store.dao

import models.domain._
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
    cultureId: Option[String] = None): Iterator[Quest]

  /**
   * Update quest's points.
   */
  def updatePoints(
    id: String,
    pointsChange: Int,
    likesChange: Int = 0,
    votersCountChange: Int = 0,
    cheatingChange: Int = 0,
    spamChange: Int = 0,
    pornChange: Int = 0): Option[Quest]

  def updateStatus(id: String, newStatus: QuestStatus.Value): Option[Quest]

  def updateInfo(id: String, newLevel: Int): Option[Quest]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit
}

