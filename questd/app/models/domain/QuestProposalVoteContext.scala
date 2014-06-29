package models.domain

import models.domain.base._

case class QuestProposalVoteContext(
  reviewingQuest: Option[QuestInfoWithID] = None,
  themeOfQuest: Option[Theme] = None,
  numberOfReviewedQuests: Int = 0)
    
