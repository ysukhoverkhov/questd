package controllers.web.helpers

import models.domain.common.{ContentType, ContentReference}

case class WSContentReference(
  contentType: String,
  storage: String,
  reference: String) {

}
object WSContentReference {

  import scala.language.implicitConversions

  implicit def toContentReference(v: WSContentReference): ContentReference = {
    ContentReference(
      contentType = ContentType.withName(v.contentType),
      storage = v.storage,
      reference = v.reference
    )
  }
}

