package models.domain

case class QuestVoteConext(
  reviewingQuestID: Option[String] = None,
  numberOfReviewedQuests: Int = 0)
    
