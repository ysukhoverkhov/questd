package models.domain

import models.domain.base._

case class QuestProposalVoteConext(
  reviewingQuest: Option[QuestInfoWithID] = None,
  numberOfReviewedQuests: Int = 0)
    
