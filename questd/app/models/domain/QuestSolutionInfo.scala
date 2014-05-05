package models.domain

import models.domain.base.ID

case class QuestSolutionInfo(
  content: ContentReference,
  icon: Option[ContentReference] = None,
  vip: Boolean = false)
