package models.domain

import models.domain.base.ID

case class QuestSolutionInfoContent(
  media: ContentReference,
  icon: Option[ContentReference] = None)

case class QuestSolutionInfo(
  content: QuestSolutionInfoContent,
  vip: Boolean = false,
  themeId: String,
  questId: String)
