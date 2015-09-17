package logic.user

import logic._
import models.domain.battle.Battle
import models.domain.quest.Quest
import models.domain.solution.Solution

/**
 * All logic about voting quests is here.
 */
trait TimeLineLogic { this: UserLogic =>

  def getPopulateTimeLineDate = nextFlipHourDate

  /**
   * Returns quests for time line.
   * @param count Number of quests to return. It may return less.
   * @return List of quests for time line.
   */
  def getRandomQuestsForTimeLine(count: Int): List[Quest] = getRandomQuests(count)

  /**
   * Returns solutions for time line.
   * @param count Number of solutions to return. It may return less.
   * @return List of solutions for time line.
   */
  def getRandomSolutionsForTimeLine(count: Int): List[Solution] = getRandomSolutions(count)

  /**
   * Returns battles for time line.
   * @param count Number of battles to return. It may return less.
   * @return List of battles for time line.
   */
  def getRandomBattlesForTimeLine(count: Int): List[Battle] = getRandomBattles(count)
}
