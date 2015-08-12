package controllers.services.socialnetworks.facebook

import controllers.services.socialnetworks.client.UserIdInApplication
import controllers.services.socialnetworks.facebook.types.UserIdWithApp

/**
 * Implementation of User id in app for facebook.
 *
 * Created by Yury on 18.09.2014.
 */
private[socialnetworks] class UserIdInApplicationFacebook (
  idWithApp: UserIdWithApp
  ) extends ItemFacebook with UserIdInApplication {

  /**
   * @return Identifier of the item in SN.
   */
  def snId = idWithApp.id

  /**
   * @return Name of the application.
   */
  def appName = idWithApp.app.namespace
}

/**
 * Companion object for UserIdInApplicationFacebook class
 */
private[socialnetworks] object UserIdInApplicationFacebook {
  def apply(idWithApp: UserIdWithApp): UserIdInApplicationFacebook = new UserIdInApplicationFacebook(idWithApp)
}

