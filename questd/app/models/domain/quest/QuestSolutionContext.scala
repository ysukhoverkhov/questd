package models.domain.quest

import models.view.QuestView


case class QuestSolutionContext(
  bookmarkedQuest: Option[QuestView] = None
  )

