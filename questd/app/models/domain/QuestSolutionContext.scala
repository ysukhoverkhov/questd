package models.domain

import models.domain.view.QuestInfoWithID


case class QuestSolutionContext(
  bookmarkedQuest: Option[QuestInfoWithID] = None
  )

