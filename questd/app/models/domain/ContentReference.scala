package models.domain

object ContentType extends Enumeration {
  type ContentType = Value

  val Photo = Value(0)
  val Video = Value(1)
}

case class ContentReference(
  contentType: ContentType.Value,
  storage: String,
  reference: String)

  