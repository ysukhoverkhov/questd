package models.domain.tag

import models.domain.common.ContentReference

case class ThemeInfo(
  media: ContentReference,
  icon: Option[ContentReference] = None,
  name: String,
  description: String)

