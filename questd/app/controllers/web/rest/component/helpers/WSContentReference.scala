package controllers.web.rest.component.helpers

import models.domain.{ContentType, ContentReference}

case class WSContentReference(
  contentType: String,
  storage: String,
  reference: String) {

}
object WSContentReference {
  implicit def toContentReference(v: WSContentReference): ContentReference = {
    ContentReference(
      contentType = ContentType.withName(v.contentType),
      storage = v.storage,
      reference = v.reference
    )
  }
}

