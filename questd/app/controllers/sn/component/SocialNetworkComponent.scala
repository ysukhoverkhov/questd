package controllers.sn.component

import controllers.sn.client.SocialNetoworkImpl

trait SocialNetworkComponent {

  protected val sn: SocialNetwork

  class SocialNetwork extends SocialNetoworkImpl {
    
  }

}

