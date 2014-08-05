package models.domain

import models.domain.base.ID
import java.util.Date

case class ThemeInfo(
  media: ContentReference,
  icon: Option[ContentReference] = None,
  name: String,
  description: String)

