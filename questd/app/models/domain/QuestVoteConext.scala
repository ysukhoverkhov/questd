package models.domain

import models.domain.base._

case class QuestVoteConext(
  reviewingQuest: Option[QuestInfoWithID] = None,
  numberOfReviewedQuests: Int = 0)
    
