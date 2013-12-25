package models.domain

import models.domain.base._

case class QuestProposalVoteContext(
  reviewingQuest: Option[QuestInfoWithID] = None,
  numberOfReviewedQuests: Int = 0)
    
