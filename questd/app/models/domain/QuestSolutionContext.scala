package models.domain

import models.domain.view.QuestView


case class QuestSolutionContext(
  bookmarkedQuest: Option[QuestView] = None
  )

