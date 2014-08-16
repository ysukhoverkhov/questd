package controllers.sn.client

import controllers.sn.facebook.SocialNetworkClientFacebook

abstract class SocialNetoworkImpl {
  
  val clients = Map(
      "FB" -> SocialNetworkClientFacebook()
      )
  
  def clientForName(name: String): Option[SocialNetworkClient] = {
    clients.get(name)
  } 
  
}

