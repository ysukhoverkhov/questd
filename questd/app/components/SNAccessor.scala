package components

import models.store._
import controllers.services.socialnetworks.component.SocialNetworkComponent

trait SNAccessor {
  val sn: SocialNetworkComponent#SocialNetwork
}

