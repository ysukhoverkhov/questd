package controllers.sn.component

import controllers.sn.client.SocialNetworkImpl

trait SocialNetworkComponent {

  protected val sn: SocialNetwork

  class SocialNetwork extends SocialNetworkImpl {

  }

}

