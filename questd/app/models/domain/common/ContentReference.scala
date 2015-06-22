package models.domain.common



case class ContentReference(
  contentType: ContentType.Value,
  storage: String,
  reference: String)

