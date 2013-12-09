package models.store.dao

import models.domain._

trait QuestSolutionDAO  extends BaseDAO[QuestSolution] {

    def allWithStatus(status: String): Iterator[QuestSolution]

}

