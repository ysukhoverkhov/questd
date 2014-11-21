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
  def getRandomQuestForTimeLine: Option[Quest] = {
    getRandomQuest
  }

  /**
   * @return None if no more quests to vote for today.
   */
  def getRandomSolutionForTimeLine: Option[Solution] = {
    getRandomSolution
  }

}
