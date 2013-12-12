package models.store.dao

import models.domain._

trait QuestSolutionDAO  extends BaseDAO[QuestSolution] {

    def allWithStatus(status: String, minLevel: Int, maxLevel: Int): Iterator[QuestSolution]

}

