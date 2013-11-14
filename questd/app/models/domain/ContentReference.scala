package models.domain

object ContentType extends Enumeration {
  type ContentType = Value

  val Photo = Value(0, "Photo")
  val Video = Value(1, "Video")
}


case class ContentReference(
  contentType: ContentType.ContentType = ContentType.Photo,
  storage: String = "",
  reference: String = "")

  