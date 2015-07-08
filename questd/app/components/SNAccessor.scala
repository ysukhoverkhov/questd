package components

import models.store._
import controllers.sn.component.SocialNetworkComponent

trait SNAccessor {
  val sn: SocialNetworkComponent#SocialNetwork
}

