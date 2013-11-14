package models.domain

object ContentType extends Enumeration {
  type ContentType = Value

  val Photo = Value(0, "Photo")
  val Video = Value(1, "Video")
}


case class ContentReference(
  contentType: Int = ContentType.Photo.id, // This is int to make it serializable to DB.
  storage: String = "",
  reference: String = "")

  