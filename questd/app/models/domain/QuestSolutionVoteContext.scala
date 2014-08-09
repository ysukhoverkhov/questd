package models.domain

import models.domain.view._

case class QuestSolutionVoteContext(
  reviewingQuestSolution: Option[QuestSolutionInfoWithID] = None,
  questOfSolution: Option[QuestInfoWithID] = None,
  numberOfReviewedSolutions: Int = 0)
    
