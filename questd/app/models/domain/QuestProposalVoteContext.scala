package models.domain

import models.domain.view._

case class QuestProposalVoteContext(
  reviewingQuest: Option[QuestInfoWithID] = None,
  themeOfQuest: Option[ThemeInfoWithID] = None,
  numberOfReviewedQuests: Int = 0)

