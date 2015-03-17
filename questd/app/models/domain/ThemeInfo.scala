package models.domain

case class ThemeInfo(
  media: ContentReference,
  icon: Option[ContentReference] = None,
  name: String,
  description: String)

