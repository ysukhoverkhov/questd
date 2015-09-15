package components

import controllers.services.socialnetworks.component.SocialNetworkComponent

trait SNAccessor {
  val sn: SocialNetworkComponent#SocialNetwork
}

