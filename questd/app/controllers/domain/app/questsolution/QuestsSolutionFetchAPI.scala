package controllers.domain.app.questsolution

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._


case class AllQuestSolutionsRequest(minLevel: Int, maxLevel: Int)
case class AllQuestSolutionsResult(quests: Iterator[QuestSolution])


private [domain] trait QuestsSolutionFetchAPI { this: DBAccessor => 


 
}


