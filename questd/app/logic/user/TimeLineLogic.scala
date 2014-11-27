package logic.user

import logic._
import models.domain._

/**
 * All logic about voting quests is here.
 */
trait TimeLineLogic { this: UserLogic =>

  /**
   * @return None if no more quests to vote for today.
   */
  def getRandomQuestsForTimeLine(count: Int): List[Quest] = {
    (for (i <- 1 to count) yield getRandomQuest).flatten.toList
    // TODO: tell what levels to use here.
  }

  /**
   * @return None if no more quests to vote for today.
   */
  def getRandomSolutionsForTimeLine(count: Int): List[Solution] = {
    (for (i <- 1 to count) yield getRandomSolution).flatten.toList
  }

  /**
   * @return None if no more quests to vote for today.
   */
  // TODO: implement me.
//  def getRandomBattleForTimeLine: Option[Solution] = {
//    getRandomSolution
//  }

}
