package models.store.dao

import models.domain._

trait QuestSolutionDAO  extends BaseDAO[QuestSolution] {

    def allWithStatusAndLevels(status: String, minLevel: Int, maxLevel: Int): Iterator[QuestSolution]
    def allWithStatusAndQuest(status: String, questId: String): Iterator[QuestSolution]

}

