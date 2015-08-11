package controllers.services.socialnetworks.client

import controllers.services.socialnetworks.exception.SocialNetworkClientNotFound
import controllers.services.socialnetworks.facebook.SocialNetworkClientFacebook

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

