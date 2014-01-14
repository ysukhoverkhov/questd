package models.store.dao

import models.domain._

trait QuestSolutionDAO extends BaseDAO[QuestSolution] {

  def allWithStatusAndLevels(status: String, minLevel: Int, maxLevel: Int): Iterator[QuestSolution]
  def allWithStatusAndQuest(status: Option[String], questId: String, skip: Int = 0): Iterator[QuestSolution]
  def allWithStatusAndUser(status: Option[String], userId: String, skip: Int = 0): Iterator[QuestSolution]

  def updateStatus(id: String, newStatus: String): Option[QuestSolution]

  def updatePoints(
    id: String,

    reviewsCountChange: Int = 0,
    pointsRandomChange: Int = 0,
    pointsFriendsChange: Int = 0,
    pointsInvitedChange: Int = 0,
    cheatingChange: Int = 0,

    spamChange: Int = 0,
    pornChange: Int = 0): Option[QuestSolution]

}

