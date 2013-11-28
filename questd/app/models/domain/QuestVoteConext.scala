package models.domain

// TODO reset count on nightly crawler.
case class QuestVoteConext(
  reviewingQuestID: Option[String] = None,
  numberOfreviewedQuests: Int = 0)
    
