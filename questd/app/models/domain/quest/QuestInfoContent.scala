package models.domain.quest

import models.domain.common.ContentReference

/**
 * Content of quest.
 */
case class QuestInfoContent(
  media: Option[ContentReference],
  icon: Option[ContentReference],
  description: String)

