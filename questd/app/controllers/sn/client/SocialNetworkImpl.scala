package controllers.sn.client

import controllers.sn.exception.SocialNetworkClientNotFound
import controllers.sn.facebook.SocialNetworkClientFacebook

abstract class SocialNetworkImpl {

  val clients = Map(
    SocialNetworkClientFacebook.Name -> SocialNetworkClientFacebook())

  def clientForName(name: String): SocialNetworkClient = {
    try {
      clients(name)
    } catch {
      case ex: NoSuchElementException =>
        throw new SocialNetworkClientNotFound
    }
  }
}

