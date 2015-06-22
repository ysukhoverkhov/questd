package models.store.dao

import models.domain.solution.{Solution, SolutionStatus}

trait SolutionDAO extends BaseDAO[Solution] {

  /**
   * Get solutions what meets following optional parameters.
   */
  def allWithParams(
    status: List[SolutionStatus.Value] = List.empty,
    authorIds: List[String] = List.empty,
    authorIdsExclude: List[String] = List.empty,
    levels: Option[(Int, Int)] = None,
    skip: Int = 0,
    vip: Option[Boolean] = None,
    ids: List[String] = List.empty,
    idsExclude: List[String] = List.empty,
    questIds: List[String] = List.empty,
    themeIds: List[String] = List.empty,
    cultureId: Option[String] = None): Iterator[Solution]

  def updateStatus(
    id: String,
    newStatus: SolutionStatus.Value): Option[Solution]

  def addParticipatedBattle(id: String, battleId: String): Option[Solution]

  def updatePoints(
    id: String,
    timelinePointsChange: Int,
    likesChange: Int = 0,
    votersCountChange: Int = 0,
    cheatingChange: Int = 0,
    spamChange: Int = 0,
    pornChange: Int = 0): Option[Solution]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit

}

