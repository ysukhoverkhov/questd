package controllers.services.socialnetworks.component

import controllers.services.socialnetworks.client.SocialNetworkImpl

trait SocialNetworkComponent {

  protected val sn: SocialNetwork

  class SocialNetwork extends SocialNetworkImpl {

  }

}

