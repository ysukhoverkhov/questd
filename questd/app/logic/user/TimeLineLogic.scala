package logic.user

import logic._
import models.domain._

/**
 * All logic about voting quests is here.
 */
trait TimeLineLogic { this: UserLogic =>

  def getPupulateTimeLineDate = getNextFlipHourDate

  /**
   * Returns quests for time line.
   * @param count Number of quests to return. It may return less.
   * @return List of quests for time line.
   */
  def getRandomQuestsForTimeLine(count: Int): List[Quest] = getRandomItemForTimeLine(count, getRandomQuest)

  /**
   * Returns solutions for time line.
   * @param count Number of solutions to return. It may return less.
   * @return List of solutions for time line.
   */
  def getRandomSolutionsForTimeLine(count: Int): List[Solution] = getRandomItemForTimeLine(count, getRandomSolution)

  /**
   * Returns battles for time line.
   * @param count Number of battles to return. It may return less.
   * @return List of battles for time line.
   */
  def getRandomBattlesForTimeLine(count: Int): List[Battle] = getRandomItemForTimeLine(count, getRandomBattle)


  // TODO: make sure battle selected on one iteration will not be included in next iteration
  private def getRandomItemForTimeLine[T](count: Int, fun: => Option[T]): List[T] = {
    (for (i <- 1 to count) yield fun).flatten.toList
  }
}
