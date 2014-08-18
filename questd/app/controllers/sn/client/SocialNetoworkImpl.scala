package controllers.sn.client

import controllers.sn.facebook.SocialNetworkClientFacebook
import controllers.sn.exception.SocialNetworkClientNotFound
import play.Logger

abstract class SocialNetoworkImpl {

  val clients = Map(
    SocialNetworkClientFacebook.Name -> SocialNetworkClientFacebook())

  def clientForName(name: String): SocialNetworkClient = {
    try {
      clients(name)
    } catch {
      case ex: NoSuchElementException => {
        throw new SocialNetworkClientNotFound
      }
    }
  }
}

