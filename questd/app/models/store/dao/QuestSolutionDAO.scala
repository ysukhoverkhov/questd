package models.store.dao

import models.domain._
// TODO: rename me to SolutionDAO
trait QuestSolutionDAO extends BaseDAO[Solution] {

  /**
   * Get solutions what meets following optional parameters.
   */
  def allWithParams(
    status: List[SolutionStatus.Value] = List(),
    authorIds: List[String] = List(),
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List(),
    questIds: List[String] = List(),
    themeIds: List[String] = List(),
    cultureId: Option[String] = None): Iterator[Solution]

  // TODO: remove rival id here.
  def updateStatus(
    id: String,
    newStatus: SolutionStatus.Value,
    rivalId: Option[String] = None): Option[Solution]

  def updatePoints(
    id: String,

    reviewsCountChange: Int = 0,
    pointsRandomChange: Int = 0,
    pointsFriendsChange: Int = 0,
    cheatingChange: Int = 0,

    spamChange: Int = 0,
    pornChange: Int = 0): Option[Solution]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit

}

