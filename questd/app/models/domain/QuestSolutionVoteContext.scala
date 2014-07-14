package models.domain

import models.domain.base._

case class QuestSolutionVoteContext(
  reviewingQuestSolution: Option[QuestSolutionInfoWithID] = None,
  questOfSolution: Option[QuestInfo] = None,
  numberOfReviewedSolutions: Int = 0)
    
