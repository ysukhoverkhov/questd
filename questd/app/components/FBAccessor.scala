package components

import models.store._
import controllers.domain.libs.facebook.FacebookComponent

trait FBAccessor {
  val fb: FacebookComponent#Facebook
}

